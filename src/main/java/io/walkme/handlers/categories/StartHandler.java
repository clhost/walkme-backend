package io.walkme.handlers.categories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.walkme.handlers.BaseHttpHandler;
import io.walkme.handlers.auth.AuthHandler;
import io.walkme.mappers.Mapper;
import io.walkme.mappers.UserToResponseUserEntityMapper;
import io.walkme.response.ResultBuilder;
import io.walkme.response.user.ResponseUserEntity;
import io.walkme.services.EntityService;
import io.walkme.services.UserService;
import io.walkme.services.fields.UserFields;
import io.walkme.storage.entities.Category;
import io.walkme.storage.entities.User;
import io.walkme.storage.entities.WalkMeCategory;
import io.walkme.utils.ResponseBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * handle: /api/start
 */
public class StartHandler extends BaseHttpHandler {
    private final Logger logger = LogManager.getLogger(StartHandler.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser jsonParser = new JsonParser();
    private final Mapper<ResponseUserEntity, User> mapper = new UserToResponseUserEntityMapper();
    private static final EntityService<User, String, UserFields> userService = new UserService();

    private static final List<Category> categories = new ArrayList<>();
    static {
        for (WalkMeCategory category : WalkMeCategory.getAll()) {
            categories.add(new Category(category.id(), category.description()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        hold((FullHttpRequest) msg);

        String[] tokens = getTokens();

        if (tokens.length < 2) {
            ctx.fireChannelRead(msg);
            return;
        }

        if (tokens[0].equals(API_PREFIX) && tokens[1].equals(API_START)) {
            JsonObject object = new JsonObject();
            String result = gson.toJson(categories);

            object.add("categories", jsonParser.parse(result));

            String token = getParams().get("token").get(0);
            User user = userService.get(token, UserFields.TOKEN);
            object.add("user", jsonParser.parse(gson.toJson(mapper.map(user))));

            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.OK,
                    ResultBuilder.asJson(200, object, ResultBuilder.ResultType.RESULT)));
            ctx.close();

            release();
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage());
        ctx.close();
        release();
    }
}
