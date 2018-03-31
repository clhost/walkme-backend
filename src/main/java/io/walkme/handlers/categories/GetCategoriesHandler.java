package io.walkme.handlers.categories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.walkme.handlers.auth.AuthHandler;
import io.walkme.response.ResultBuilder;
import io.walkme.storage.entities.Category;
import io.walkme.storage.entities.WalkMeCategory;
import io.walkme.utils.ResponseBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class GetCategoriesHandler extends ChannelInboundHandlerAdapter {
    private static final String API_PREFIX = "api";
    private static final String API_CATEGORIES = "getCategories";

    private final Logger logger = LogManager.getLogger(AuthHandler.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser jsonParser = new JsonParser();

    private static final List<Category> categories = new ArrayList<>();
    static {
        for (WalkMeCategory category : WalkMeCategory.getAll()) {
            categories.add(new Category(category.id(), category.description()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            ctx.fireChannelRead(msg);
            return;
        }

        FullHttpRequest request = (FullHttpRequest) msg;

        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        String[] tokens = decoder.path().substring(1).split("/");

        if (tokens.length < 2) {
            ctx.fireChannelRead(msg);
            return;
        }

        if (tokens[0].equals(API_PREFIX) && tokens[1].equals(API_CATEGORIES)) {
            JsonObject object = new JsonObject();
            String result = gson.toJson(categories);

            object.add("categories", jsonParser.parse(result));

            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.OK,
                    ResultBuilder.asJson(200, object, ResultBuilder.ResultType.RESULT)));
            ctx.close();
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
