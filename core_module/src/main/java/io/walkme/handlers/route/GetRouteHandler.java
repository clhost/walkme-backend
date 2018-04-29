package io.walkme.handlers.route;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.walkme.handlers.BaseHttpHandler;
import io.walkme.response.ResponseBuilder;
import io.walkme.response.ResultBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import route.core.RouteService;

import java.util.List;
import java.util.Map;

/**
 * handle: /api/getRoute
 * params (json): token, lat, lng, categories
 */
public class GetRouteHandler extends BaseHttpHandler {
    private static final JsonParser jsonParser = new JsonParser();
    private static final String PARAM_LAT = "lat";
    private static final String PARAM_LNG = "lng";
    private static final String PARAM_CATEGORIES = "categories";

    private final Logger logger = LogManager.getLogger(GetRouteHandler.class);
    private final RouteService routeService;

    public GetRouteHandler(RouteService routeService) {
        this.routeService = routeService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        hold((FullHttpRequest) msg);

        String[] tokens = getTokens();
        Map<String, List<String>> params = getParams();

        if (tokens.length < 2) {
            ctx.fireChannelRead(msg);
        } else if (tokens[0].equals(API_PREFIX) && tokens[1].equals(API_GET_ROUTE)) {
            if (checkParams(params)) {
                handleRoute(ctx, params);
            } else {
                ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                        HttpResponseStatus.BAD_REQUEST,
                        ResponseBuilder.JSON_BAD_RESPONSE));
                ctx.close();
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void handleRoute(ChannelHandlerContext ctx, Map<String, List<String>> params) throws Exception {
        try {
            String route = routeService.getRoute(
                    Double.parseDouble(params.get(PARAM_LAT).get(0)),
                    Double.parseDouble(params.get(PARAM_LNG).get(0)),
                    new int[]{});

            JsonObject jsonObject = jsonParser.parse(route).getAsJsonObject();
            if (jsonObject.get("error") != null) {
                ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                        HttpResponseStatus.OK,
                        ResultBuilder.asJson(500, jsonObject.get("error"), ResultBuilder.ResultType.ERROR)));
                return;
            }

            JsonObject resultJsonObject;
            if (jsonObject.get("route") != null && jsonObject.get("route").isJsonObject()) {
                resultJsonObject = jsonObject.get("route").getAsJsonObject();
                ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                        HttpResponseStatus.OK,
                        ResultBuilder.asJson(200, resultJsonObject, ResultBuilder.ResultType.RESULT)));
                return;
            }

            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.OK,
                    ResponseBuilder.JSON_BAD_GATEWAY_RESPONSE));
        } finally {
            ctx.close();
            release();
        }
    }

    private boolean checkParams(Map<String, List<String>> params) {
        return params.get("lat") != null &&
                params.get("lat").get(0) != null &&
                params.get("lng") != null &&
                params.get("lng").get(0) != null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage());
        cause.printStackTrace();
        ctx.close();
        release();
    }
}
