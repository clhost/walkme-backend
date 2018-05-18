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
import route.services.CategoryService;
import route.storage.entities.WalkMeCategory;

import java.util.Arrays;
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
    private static final String NEAR = "near";
    //private static final String AVG_CHECK = "avgCheck";

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
                release();
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void handleRoute(ChannelHandlerContext ctx, Map<String, List<String>> params) throws Exception {
        try {
            String[] strCategories = params.get(PARAM_CATEGORIES).get(0).split(",");
            int[] categories = new int[strCategories.length];
            for (int i = 0; i < categories.length; i++) {
                categories[i] = Integer.parseInt(strCategories[i]);
            }

            String route = routeService.getRoute(
                    Double.parseDouble(params.get(PARAM_LAT).get(0)),
                    Double.parseDouble(params.get(PARAM_LNG).get(0)),
                    categories);

            JsonObject jsonObject = jsonParser.parse(route).getAsJsonObject();
            if (jsonObject.get("error") != null) {
                ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                        HttpResponseStatus.OK,
                        ResultBuilder.asJson(
                                500,
                                jsonObject.get("error").getAsString(),
                                ResultBuilder.ResultType.ERROR)));
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
        boolean notNull = params.get(PARAM_LAT) != null &&
                params.get(PARAM_LAT).get(0) != null &&
                params.get(PARAM_LNG) != null &&
                params.get(PARAM_LNG).get(0) != null &&
                params.get(PARAM_CATEGORIES) != null &&
                params.get(PARAM_CATEGORIES).get(0) != null &&
                params.get(NEAR) != null &&
                params.get(NEAR).get(0) != null /*&&
                params.get(AVG_CHECK) != null &&
                params.get(AVG_CHECK).get(0) != null*/;
        if (!notNull) {
            return false;
        }

        boolean isCategoriesValid = true;
        for (String num : params.get(PARAM_CATEGORIES).get(0).split(",")) {
            if (!(isNumeric(num) && categoryContains(Integer.parseInt(num)))) {
                isCategoriesValid = false;
            }
        }
        return isCategoriesValid &&
                isNumeric(params.get(PARAM_LAT).get(0)) &&
                isNumeric(params.get(PARAM_LNG).get(0)) &&
                //isNumeric(params.get(AVG_CHECK).get(0)) &&
                params.get(NEAR).get(0).matches("[0]|[1]");
    }

    private boolean categoryContains(int c) {
        for (WalkMeCategory category : WalkMeCategory.getAll()) {
            if (c == category.id()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage());
        cause.printStackTrace();
        ctx.close();
        release();
    }
}
