package utils;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.http.*;

public class ResponseBuilder {
    public static final String JSON_BAD_REQUEST = "{ \"status: \"400, \"error: \"bad request }";

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
