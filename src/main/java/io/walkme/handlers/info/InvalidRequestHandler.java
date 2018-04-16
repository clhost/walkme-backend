package io.walkme.handlers.info;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.walkme.handlers.BaseHttpHandler;
import io.walkme.utils.ResponseBuilder;

public class InvalidRequestHandler extends BaseHttpHandler {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        hold((FullHttpRequest) msg);

        ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(HttpResponseStatus.BAD_REQUEST,
                ResponseBuilder.JSON_BAD_RESPONSE));
        ctx.close();
        release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
        release();
    }
}
