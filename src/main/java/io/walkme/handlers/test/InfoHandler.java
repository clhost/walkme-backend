package io.walkme.handlers.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Show full information about incoming request
 */
public class InfoHandler extends ChannelInboundHandlerAdapter {
    private final boolean isFaviconIgnore;
    private final Logger logger = LogManager.getLogger(InfoHandler.class);

    public InfoHandler(boolean isFaviconIgnore) {
        this.isFaviconIgnore = isFaviconIgnore;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            ctx.fireChannelRead(msg);
            return;
        }

        FullHttpRequest request = (FullHttpRequest) msg;

        byte[] content = new byte[request.content().readableBytes()];
        request.content().readBytes(content);

        if (isFaviconIgnore && !request.uri().contains("favicon.ico")) {
            logger.info("Incoming connection: " + ctx.channel().remoteAddress());
            System.out.println("Incoming request: ");
            System.out.println("* Uri: " + request.uri());
            System.out.println("* Headers: " + request.headers());
            System.out.println("* Method: " + request.method());
            System.out.println("* Content: " + new String(content));
        }

        ctx.fireChannelRead(msg);
    }
}
