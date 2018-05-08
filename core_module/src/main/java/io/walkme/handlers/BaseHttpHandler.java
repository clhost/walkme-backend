package io.walkme.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.walkme.core.ServerMode;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseHttpHandler extends ChannelInboundHandlerAdapter {
    private FullHttpRequest request;
    private String[] tokens;
    private Map<String, List<String>> params;
    protected static final String API_PREFIX = "api";
    protected static final String API_AUTH = "auth";
    protected static final String API_FAKE = "fake";
    protected static final String API_LOGOUT = "logout";
    protected static final String API_START = "start";
    protected static final String API_GET_ROUTE = "getRoute";
    protected static final String API_SAVE_ROUTE = "saveRoute";
    protected static final String API_GET_SAVED_ROUTES = "getSavedRoutes";
    private static Set<String> set = new HashSet<>();

    static {
        set.add(API_PREFIX);
        set.add(API_AUTH);
        set.add(API_FAKE);
        set.add(API_LOGOUT);
        set.add(API_START);
        set.add(API_GET_ROUTE);
        set.add(API_SAVE_ROUTE);
        set.add(API_GET_SAVED_ROUTES);
    }

    protected static Set<String> set() {
        return set;
    }

    protected boolean check(Object msg) {
        return msg instanceof FullHttpRequest;
    }

    protected boolean checkAuth() {
        return ServerMode.getAuth();
    }

    protected void hold(FullHttpRequest request) {
        this.request = request;

        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        tokens = decoder.path().substring(1).split("/");
        params = decoder.parameters();
    }

    protected String[] getTokens() throws IllegalStateException {
        check();
        return tokens;
    }

    protected Map<String, List<String>> getParams() {
        check();
        return params;
    }

    protected HttpMethod method() {
        return request.method();
    }

    protected byte[] readBody() throws IllegalStateException {
        if (request.method().equals(HttpMethod.POST)) {
            ByteBuf buf = request.content();
            byte[] content = new byte[buf.readableBytes()];
            while (buf.isReadable()) {
                content[buf.readerIndex()] = buf.readByte();
            }
            return content;
        } else {
            throw new IllegalStateException("Only POST request has a body");
        }
    }

    protected void release() {
        while (request.refCnt() != 0) {
            request.release(request.refCnt());
        }
    }

    private void check() throws IllegalStateException {
        if (request == null) {
            throw new IllegalStateException("Held object must not be null.");
        }
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
