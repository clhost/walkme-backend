package io.walkme.handlers.route;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.walkme.handlers.BaseHttpHandler;
import io.walkme.response.ResponseBuilder;
import io.walkme.response.ResultBuilder;
import route.core.RouteService;

import java.util.List;
import java.util.Map;

/**
 * handle: /api/isPointAvailable
 * params (json): token, lat, lng
 */
public class IsPointAvailableHandler extends BaseHttpHandler {
    private static final String PARAM_LAT = "lat";
    private static final String PARAM_LNG = "lng";
    private final RouteService routeService;

    public IsPointAvailableHandler(RouteService routeService) {
        this.routeService = routeService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        hold((FullHttpRequest) msg);

        String[] tokens = getTokens();
        Map<String, List<String>> params = getParams();

        if (tokens.length < 2) {
            ctx.fireChannelRead(msg);
        } else if (tokens[0].equals(API_PREFIX) && tokens[1].equals(API_IS_POINT_AVAILABLE)) {
            if (checkParams(params)) {
                handle(ctx, params);
            } else {
                ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                        HttpResponseStatus.BAD_REQUEST,
                        ResponseBuilder.JSON_BAD_RESPONSE));
                ctx.close();
                release();
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void handle(ChannelHandlerContext ctx, Map<String, List<String>> params) {
        try {
            double lat = Double.parseDouble(params.get(PARAM_LAT).get(0));
            double lng = Double.parseDouble(params.get(PARAM_LNG).get(0));
            boolean isPointAvailable = routeService.isPointAvailable(lat, lng);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("isPointAvailable", isPointAvailable);
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.OK,
                    ResultBuilder.asJson(200, jsonObject, ResultBuilder.ResultType.RESULT)));
        } finally {
            ctx.close();
            release();
        }
    }

    private boolean checkParams(Map<String, List<String>> params) {
        boolean notNull = params.get(PARAM_LAT) != null &&
                params.get(PARAM_LAT).get(0) != null &&
                params.get(PARAM_LNG) != null &&
                params.get(PARAM_LNG).get(0) != null;
        return notNull && isNumeric(params.get(PARAM_LAT).get(0)) && isNumeric(params.get(PARAM_LNG).get(0));
    }
}
