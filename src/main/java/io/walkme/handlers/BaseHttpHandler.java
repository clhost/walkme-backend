package io.walkme.handlers;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

public abstract class BaseHttpHandler extends ChannelInboundHandlerAdapter {
    private FullHttpRequest request;
    private String[] tokens;
    private Map<String, List<String>> params;

    protected static final String API_PREFIX = "api";

    protected boolean check(Object msg) {
        return msg instanceof FullHttpRequest;
    }

    protected void hold(FullHttpRequest request) {
        this.request = request;

        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        tokens = decoder.path().substring(1).split("/");
        params = decoder.parameters();
    }

    protected String[] getTokens() throws IllegalStateException {
        if (request == null) {
            throw new IllegalStateException("Held object must not be null.");
        }

        return tokens;
    }

    protected Map<String, List<String>> getParams() {
        if (request == null) {
            throw new IllegalStateException("Held object must not be null.");
        }

        return params;
    }
}
