package io.walkme.handlers.route;

import auth.core.AuthService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.walkme.handlers.BaseHttpHandler;
import io.walkme.response.ResponseBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import route.core.RouteService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * handle: /api/saveRoute
 * params: token
 * body (json): путь, который нужно сохранить
 */
public class SaveRouteHandler extends BaseHttpHandler {
    private static final JsonParser jsonParser = new JsonParser();
    private final Logger logger = LogManager.getLogger(SaveRouteHandler.class);
    private final RouteService routeService;
    private final AuthService authService;

    public SaveRouteHandler(RouteService routeService, AuthService authService) {
        this.routeService = routeService;
        this.authService = authService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        hold((FullHttpRequest) msg);

        String[] tokens = getTokens();

        try {
            if (!method().equals(HttpMethod.POST)) {
                ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                        HttpResponseStatus.BAD_REQUEST,
                        ResponseBuilder.JSON_BAD_RESPONSE));
                return;
            }

            if (tokens.length < 2) {
                ctx.fireChannelRead(msg);
            } else if (tokens[0].equals(API_PREFIX) && tokens[1].equals(API_SAVE_ROUTE)) {
                saveRoute(ctx, readBody());
            }
        } finally {
            ctx.close();
            release();
        }
    }

    private void saveRoute(ChannelHandlerContext ctx, byte[] route) {
        String token = getParams().get("token").get(0);
        String jsonRoute = new String(route, StandardCharsets.UTF_8);

        String userInfo = authService.getUserInfo(token);
        if (userInfo == null) {
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.OK,
                    ResponseBuilder.JSON_BAD_GATEWAY_RESPONSE));
            ctx.close();
            release();
            return;
        }

        JsonObject user = jsonParser.parse(userInfo).getAsJsonObject();
        String id = user.get("social_id").getAsString();

        routeService.saveRoute(id, jsonRoute);
    }
}
