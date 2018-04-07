package io.walkme.handlers.auth;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
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
            ctx.fireChannelRead(msg);
            return;
        }

        hold((FullHttpRequest) msg);

        String[] tokens = getTokens();
        Map<String, List<String>> params = getParams();

        if (tokens.length > 1 && tokens[1].equals(API_AUTH)) {
            ctx.fireChannelRead(msg);
            return;
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
