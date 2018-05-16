package io.walkme.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.walkme.handlers.BaseHttpHandler;
import io.walkme.helpers.ConfigHelper;

import java.util.Map;


public class RedirectHandler extends BaseHttpHandler {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        log(fullHttpRequest);

        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.TEMPORARY_REDIRECT);
        fullHttpResponse.headers().set(
                HttpHeaderNames.LOCATION,
                ConfigHelper.FULL_DOMAIN + fullHttpRequest.uri());
        ctx.writeAndFlush(fullHttpResponse);
        ctx.close();
    }

    private void log(FullHttpRequest request) {
        System.out.println("=====================================================");
        System.out.println("Incoming request: ");
        System.out.println("* Uri: " + request.uri());
        System.out.println("* Protocol: " + request.protocolVersion());
        System.out.println("* Headers: \t");

        for (Map.Entry<String, String> header : request.headers()) {
            if (!header.getKey().equalsIgnoreCase(HttpHeaderNames.COOKIE.toString())) {
                System.out.println("\t" + header.getKey() + ": " + header.getValue() + "\t");
            }
        }

        System.out.println("* Method: " + request.method());
        System.out.println("=====================================================");
    }
}
