package io.walkme.handlers.auth;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.walkme.core.ServerMode;
import io.walkme.handlers.BaseHttpHandler;
import io.walkme.services.SessionService;
import io.walkme.utils.ResponseBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class TokenHandler extends BaseHttpHandler {
    private final Logger logger = LogManager.getLogger(TokenHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!check(msg)) {
            // ignore not http requests
            return;
        }

        hold((FullHttpRequest) msg);

        String[] tokens = getTokens();
        Map<String, List<String>> params = getParams();

        if (!checkAuth()) { // auth off
            ctx.fireChannelRead(msg);
            return;
        }

        // if request to auth
        if (tokens.length > 1 && tokens[1].equals(API_AUTH)) {
            ctx.fireChannelRead(msg);
            return;
        }

        // if main page
        if (tokens.length == 1 && tokens[0].equals("")) {
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(HttpResponseStatus.OK, "hi"));
            return;
        }

        // if api
        if (tokens.length == 1 && tokens[0].equals(API_PREFIX)) {
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.BAD_REQUEST,
                    ResponseBuilder.JSON_BAD_RESPONSE));
            ctx.close();
            return;
        }

        for (String t : tokens) {
            if (!set().contains(t)) {
                ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                        HttpResponseStatus.BAD_REQUEST,
                        ResponseBuilder.JSON_BAD_RESPONSE));
                ctx.close();
                return;
            }
        }

        if (params.get("token") != null &&
                params.get("token").size() > 0 &&
                SessionService.getInstance().isSessionExist(params.get("token").get(0))) {
            ctx.fireChannelRead(msg);
        } else {
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.FORBIDDEN,
                    ResponseBuilder.JSON_UNAUTHORIZED_RESPONSE));
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage());
        cause.printStackTrace();
    }
}
