package io.walkme.handlers.info;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.walkme.handlers.BaseHttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Show full information about incoming request
 */
public class InfoHandler extends BaseHttpHandler {
    private final boolean isFaviconIgnore;
    private final Logger logger = LogManager.getLogger(InfoHandler.class);

    public InfoHandler(boolean isFaviconIgnore) {
        this.isFaviconIgnore = isFaviconIgnore;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!check(msg)) {
            ctx.fireChannelRead(msg);
            return;
        }

        FullHttpRequest request = (FullHttpRequest) msg;

        if (isFaviconIgnore && !request.uri().contains("favicon.ico")) {
            logger.info("Incoming connection: " + ctx.channel().remoteAddress() + ", uri: " + request.uri());
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
        }

        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //logger.info("connection: " + ctx.channel().remoteAddress() + " closed.");
        //ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage());
        ctx.close();
        release();
    }
}
