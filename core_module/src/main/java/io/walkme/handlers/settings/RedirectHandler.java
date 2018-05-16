package io.walkme.handlers.settings;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.walkme.handlers.BaseHttpHandler;


public class RedirectHandler extends BaseHttpHandler {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        fullHttpRequest.protocolVersion();

        if (fullHttpRequest.protocolVersion().toString().toLowerCase().contains("http")) {
            FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.TEMPORARY_REDIRECT);
            fullHttpResponse.headers().setAll(fullHttpRequest.headers());
            fullHttpResponse.headers().set(
                    HttpHeaderNames.LOCATION,
                    "https://" + fullHttpRequest.uri().replaceAll("http://", ""));
            ctx.writeAndFlush(fullHttpResponse);
        }
    }
}
