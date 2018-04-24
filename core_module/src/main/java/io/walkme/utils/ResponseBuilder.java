package io.walkme.utils;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResponseBuilder {
    public static final String JSON_BAD_RESPONSE = "{ \n" +
            "   \"status\": 400, \n" +
            "   \"error\": \"bad request\" \t\n" +
            "}";
    public static final String JSON_NOT_FOUND_RESPONSE = "{ \n" +
            "   \"status\": 404, \n" +
            "   \"error\": \"not found\" \t\n" +
            "}";
    public static final String JSON_UNAUTHORIZED_RESPONSE = "{ \n" +
            "   \"status\": 403, \n" +
            "   \"error\": \"unauthorized\" \t\n" +
            "}";
    public static final String JSON_LOGOUT_RESPONSE = "{ \n" +
            "   \"status\": 200, \n" +
            "   \"result\": \"logout\" \t\n" +
            "}";
    public static final String JSON_FAKE_RESPONSE = " ";

    public static final String JSON_STUB_BAD_RESPONSE = "{ \n" +
            "   \"status\": 400, \n" +
            "   \"error\": \"supported only stub version\" \t\n" +
            "}";
    public static final String JSON_START_POINT_UNAVAILABLE_RESPONSE = "{ \n" +
            "   \"status\": 500, \n" +
            "   \"error\": \"start point unavailable\" \t\n" +
            "}";
    public static final String JSON_NOT_ENOUGH_POINTS_RESPONSE = "{ \n" +
            "   \"status\": 500, \n" +
            "   \"error\": \"not enough points\" \t\n" +
            "}";

    /**
     * @param status response status
     * @param data data into body
     */
    public static FullHttpResponse buildJsonResponse(HttpResponseStatus status, String data) {
        byte[] bytes = data.getBytes();

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(bytes));

        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);

        return response;
    }

    public static FullHttpResponse buildStaticResponse(HttpResponseStatus status, String path, String contentType)
            throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer(bytes));

        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);

        return response;
    }
}
