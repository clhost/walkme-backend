package io.walkme.handlers.route;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.walkme.handlers.BaseHttpHandler;

/**
 * handle: /api/getSavedRoutes
 * params: token
 */
public class GetSavedRoutesHandler extends BaseHttpHandler {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        hold((FullHttpRequest) msg);

        String[] tokens = getTokens();

        try {

        } finally {
            ctx.close();
            release();
        }
    }
}
