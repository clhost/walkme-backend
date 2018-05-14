package io.walkme.handlers.auth;

import auth.core.AuthService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.walkme.handlers.BaseHttpHandler;
import io.walkme.response.ResponseBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class TokenHandler extends BaseHttpHandler {
    private final Logger logger = LogManager.getLogger(TokenHandler.class);
    private final AuthService authService;

    public TokenHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!check(msg)) {
            // ignore not http requests
            return;
        }

        hold((FullHttpRequest) msg);

        String[] tokens = getTokens();
        Map<String, List<String>> params = getParams();

        // if main page
        if (tokens.length == 1 && tokens[0].equals("")) {
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(HttpResponseStatus.OK, "hi"));
            release();
            return;
        }

        if (!checkAuth()) { // auth off
            ctx.fireChannelRead(msg);
            return;
        }

        // if request to auth
        if (tokens.length > 1 && (tokens[1].equals(API_AUTH) || tokens[1].equals(API_FAKE))) {
            ctx.fireChannelRead(msg);
            return;
        }

        // if api
        if (tokens.length == 1 && tokens[0].equals(API_PREFIX)) {
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.BAD_REQUEST,
                    ResponseBuilder.JSON_BAD_RESPONSE));
            release();
            return;
        }

        for (String t : tokens) {
            if (!set().contains(t)) {
                ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                        HttpResponseStatus.BAD_REQUEST,
                        ResponseBuilder.JSON_BAD_RESPONSE));
                release();
                return;
            }
        }

        if (params.get("token") != null && params.get("token").size() > 0 &&
                authService.isUserAuthorized(params.get("token").get(0))) {
            ctx.fireChannelRead(msg);
        } else {
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.FORBIDDEN,
                    ResponseBuilder.JSON_UNAUTHORIZED_RESPONSE));
            release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage());
        ctx.close();
        release();
    }
}
