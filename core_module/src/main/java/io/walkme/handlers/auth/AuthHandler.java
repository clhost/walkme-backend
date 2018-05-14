package io.walkme.handlers.auth;

import auth.core.AuthService;
import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import io.walkme.handlers.BaseHttpHandler;
import io.walkme.response.ResponseBuilder;
import io.walkme.response.ResultBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
/**
 * handle: /api/auth
 * params: code, state
 *
 * Вызывается по нажатию кнопки на фронте "зайти под соц.сетью"
 * ВК: /api/auth&...state=vk
 * OK: /api/auth&...state=ok
 * ФБ: /api/auth&...state=fb
 *
 * return: "token": "token_string";
 * example: /api/auth&code=km32DEd&state=vk
 */
public class AuthHandler extends BaseHttpHandler {
    private static final String VK = "vk";
    private static final String OK = "ok";
    private static final String FB = "fb";
    private static final String STATE = "state";

    private final Logger logger = LogManager.getLogger(AuthHandler.class);
    private final AuthService authService;

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        hold((FullHttpRequest) msg);

        String[] tokens = getTokens();
        Map<String, List<String>> params = getParams();

        if (tokens.length < 2) {
            ctx.fireChannelRead(msg);
        } else if (tokens[0].equals(API_PREFIX) && tokens[1].equals(API_FAKE)) {
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.OK,
                    ResponseBuilder.JSON_FAKE_RESPONSE));
            //ctx.close();
            release();
        } else if (tokens[0].equals(API_PREFIX) && tokens[1].equals(API_AUTH)) {
            if (!checkAuth()) { // auth off
                ctx.fireChannelRead(msg);
                return;
            }

            if (params.get("code") != null && params.get("code").size() > 0) {
                handleAuth(ctx, params);
            } else {
                ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                        HttpResponseStatus.BAD_REQUEST,
                        ResponseBuilder.JSON_BAD_RESPONSE));
                //ctx.close();
                release();
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void handleAuth(ChannelHandlerContext ctx, Map<String, List<String>> params) throws Exception {
        try {
            switch (params.get(STATE).get(0)) {
                case VK:
                case OK:
                case FB:
                    String code = params.get("code").get(0);
                    String state = params.get(STATE).get(0).substring(0, 2);
                    String token = authorizeAndGetToken(code, state);
                    if (token != null) {
                        ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                                HttpResponseStatus.OK,
                                ResultBuilder.asJson(200, wrapToken(token), ResultBuilder.ResultType.RESULT)));
                        //ctx.close();
                    } else {
                        ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                                HttpResponseStatus.BAD_GATEWAY,
                                ResponseBuilder.JSON_BAD_GATEWAY_RESPONSE));
                        //ctx.close();
                    }
                    return;
                default:
                    ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                            HttpResponseStatus.BAD_REQUEST,
                            ResponseBuilder.JSON_BAD_RESPONSE));
                    //ctx.close();
            }
        } finally {
            release();
        }
    }

    private String authorizeAndGetToken(String code, String state) {
        return authService.authorize(code, state);
    }

    private JsonObject wrapToken(String token) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", token);
        return jsonObject;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage());
        cause.printStackTrace();
        ctx.close();
        release();
    }
}
