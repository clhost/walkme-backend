package io.walkme.handlers.auth;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import io.walkme.utils.ResponseBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * handle: /api/auth
 * params: code, state
 *
 * Вызывается по нажатию кнопки на фронте "зайти под соц.сетью"
 * ВК: /api/auth&state=vk
 * OK: /api/auth&state=ok
 *
 * return: { "token": token_string; }
 * example: /api/auth&code=km32DEd&state=vk
 */
public class AuthHandler extends ChannelInboundHandlerAdapter {
    private static final String VK = "vk";
    private static final String OK = "ok";
    private static final String STATE = "state";

    private static final String API_FAKE = "fake";
    private static final String API_AUTH = "auth";
    private static final String API_PREFIX = "api";

    private final Logger logger = LogManager.getLogger(AuthHandler.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            ctx.fireChannelRead(msg);
            return;
        }

        FullHttpRequest request = (FullHttpRequest) msg;

        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        String[] tokens = decoder.path().substring(1).split("/");
        Map<String, List<String>> params = decoder.parameters();


        if (tokens.length < 2) {
            ctx.fireChannelRead(msg);
        } else if (tokens[0].equals(API_PREFIX) && tokens[1].equals(API_FAKE)) {
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.OK,
                    ResponseBuilder.JSON_FAKE_REQUEST
            ));
            ctx.close();
        } else if (tokens[0].equals(API_PREFIX) && tokens[1].equals(API_AUTH)) {
            handleAuth(ctx, params);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void handleAuth(ChannelHandlerContext ctx, Map<String, List<String>> params) throws Exception {
        switch (params.get(STATE).get(0)) {
            case VK:
                new OAuthVk().handle(ctx, params);
                break;
            case OK:
                new OAuthOk().handle(ctx, params);
                break;
            default:
                ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                        HttpResponseStatus.BAD_REQUEST,
                        ResponseBuilder.JSON_BAD_REQUEST));
                ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //logger.error(cause.getMessage());
        cause.printStackTrace();
    }
}
