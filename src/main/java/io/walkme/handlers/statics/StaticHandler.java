package io.walkme.handlers.statics;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.walkme.handlers.BaseHttpHandler;
import io.walkme.utils.ResponseBuilder;

import java.io.File;
import java.io.IOException;


/**
 * Токен не требуется.
 * Example: static/pic/avatar.png
 */
public class StaticHandler extends BaseHttpHandler {
    private static final String STATIC_PREFIX = "static";
    private static final String PIC = "pic";

    private static final String PIC_PATH = STATIC_PREFIX + File.separator + PIC + File.separator;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        hold((FullHttpRequest) msg);

        String[] tokens = getTokens();

        if (tokens.length == 3 && tokens[0].equals(STATIC_PREFIX)) {

            try {
                switch (tokens[1]) {
                    case PIC:
                        File file = new File(PIC_PATH + tokens[2]);
                        if (file.exists()) {
                            ctx.writeAndFlush(ResponseBuilder.buildStaticResponse(
                                    HttpResponseStatus.OK,
                                    PIC_PATH + tokens[2],
                                    "image/" + tokens[2].split("\\.")[1]));
                        } else {
                            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                                    HttpResponseStatus.NOT_FOUND,
                                    ResponseBuilder.JSON_NOT_FOUND_RESPONSE));
                        }
                        break;
                }
            } catch (IOException e) {
                ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                        HttpResponseStatus.NOT_FOUND,
                        ResponseBuilder.JSON_NOT_FOUND_RESPONSE));
            } finally {
                ctx.close();
                release();
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
