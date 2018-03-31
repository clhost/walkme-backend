package io.walkme.handlers.auth;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.walkme.services.SessionService;
import io.walkme.utils.ResponseBuilder;

import java.util.List;
import java.util.Map;

public class TokenHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            ctx.fireChannelRead(msg);
            return;
        }

        FullHttpRequest request = (FullHttpRequest) msg;

        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params = decoder.parameters();

        if (params.get("code") != null) {
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
                    ResponseBuilder.JSON_UNAUTHORIZED_REQUEST));
            ctx.close();
        }
    }
}
