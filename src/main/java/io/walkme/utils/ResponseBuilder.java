package io.walkme.utils;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

public class ResponseBuilder {
    public static final String JSON_BAD_REQUEST = "{ \n" +
            "   \"result\": {\n" +
            "       \"status\": 400, \n" +
            "       \"error\": bad request \t\n" +
            "   }\n" +
            "}";
    public static final String JSON_UNAUTHORIZED_REQUEST = "{ \n" +
            "   \"result\": {\n" +
            "       \"status\": 403, \n" +
            "       \"error\": unauthorized \t\n" +
            "   }\n" +
            "}";
    public static final String JSON_FAKE_REQUEST = "{ \n" +
            "   \"result\": {\n" +
            "       \"status\": 200, \n" +
            "       \"message\": fake success \t\n" +
            "   }\n" +
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
}
