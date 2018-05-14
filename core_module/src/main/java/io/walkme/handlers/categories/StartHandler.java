package io.walkme.handlers.categories;

import auth.core.AuthService;
import com.google.gson.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.walkme.handlers.BaseHttpHandler;
import io.walkme.helpers.ConfigHelper;
import io.walkme.response.ResponseBuilder;
import io.walkme.response.ResultBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import route.core.RouteService;


/**
 * handle: /api/start
 */
public class StartHandler extends BaseHttpHandler {
    private static final String AVATAR = "avatar";
    private static final String EMPTY_AVATAR = "/static/pic/profile-empty.png";
    private static final JsonParser jsonParser = new JsonParser();
    private final Logger logger = LogManager.getLogger(StartHandler.class);
    private final AuthService authService;
    private final RouteService routeService;

    public StartHandler(AuthService  authService, RouteService routeService) {
        this.authService = authService;
        this.routeService = routeService;
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
            String token = getParams().get("token").get(0);
            String userInfo = authService.getUserInfo(token);
            String categoriesInfo = routeService.getCategories();

            if (userInfo == null || categoriesInfo == null) {
                ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                        HttpResponseStatus.OK,
                        ResponseBuilder.JSON_BAD_GATEWAY_RESPONSE));
                release();
                return;
            }

            JsonObject object = new JsonObject();
            JsonObject user = checkNullAvatar(jsonParser.parse(userInfo).getAsJsonObject());
            JsonArray categories = jsonParser.parse(categoriesInfo).getAsJsonArray();

            object.add("user", user);
            object.add("categories", categories);

            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.OK,
                    ResultBuilder.asJson(200, object, ResultBuilder.ResultType.RESULT)));
            release();
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private JsonObject checkNullAvatar(JsonObject object) {
        if (object.get(AVATAR).isJsonNull()) {
            object.addProperty("avatar", ConfigHelper.FULL_DOMAIN + EMPTY_AVATAR);
        }
        return object;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage());
        ctx.close();
        release();
    }
}
