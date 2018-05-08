package io.walkme.handlers.route;

import auth.core.AuthService;
import com.google.gson.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.walkme.handlers.BaseHttpHandler;
import io.walkme.response.ResponseBuilder;
import io.walkme.response.ResultBuilder;
import route.core.RouteService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * handle: /api/getSavedRoutes
 * params: token, limit, offset
 */
public class GetSavedRoutesHandler extends BaseHttpHandler {
    private static final String LIMIT_PARAM = "limit";
    private static final String OFFSET_PARAM = "offset";
    private static final JsonParser jsonParser = new JsonParser();
    private final RouteService routeService;
    private final AuthService authService;

    public GetSavedRoutesHandler(RouteService routeService, AuthService authService) {
        this.routeService = routeService;
        this.authService = authService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        hold((FullHttpRequest) msg);

        String[] tokens = getTokens();

        try {
            if (tokens.length < 2) {
                ctx.fireChannelRead(msg);
            } else if (tokens[0].equals(API_PREFIX) && tokens[1].equals(API_GET_SAVED_ROUTES)) {
                List<String> routes = getSavedRoutes();
                send(ctx, routes);
            } else {
                ctx.fireChannelRead(msg);
            }
        } catch (IllegalParamsException e) {
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.OK,
                    ResponseBuilder.JSON_BAD_RESPONSE));
        } catch (InternalServerErrorException e) {
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.OK,
                    ResponseBuilder.JSON_BAD_GATEWAY_RESPONSE));
        } finally {
            ctx.close();
            release();
        }
    }

    private void send(ChannelHandlerContext ctx, List<String> routes) {
        JsonArray objects = new JsonArray();
        for (String str : routes) {
            objects.add(jsonParser.parse(str).getAsJsonObject());
        }

        JsonObject object = new JsonObject();
        object.add("ways", objects);
        ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(HttpResponseStatus.OK,
                ResultBuilder.asJson(200, object, ResultBuilder.ResultType.RESULT)));
    }

    private List<String> getSavedRoutes() throws InternalServerErrorException, IllegalParamsException {
        if (!checkParams(getParams())) {
            throw new IllegalParamsException();
        }

        String token = getParams().get("token").get(0);
        String userInfo = authService.getUserInfo(token);
        if (userInfo == null) {
            throw new InternalServerErrorException();
        }

        JsonObject user = jsonParser.parse(userInfo).getAsJsonObject();
        String id = user.get("id").getAsString();
        List<String> routes = routeService.getSavedRoutes(id);

        if (routes == null) {
            return Collections.emptyList();
        }

        int offset = Integer.parseInt(getParams().get(OFFSET_PARAM).get(0));
        /*
         * +1 для того, чтобы метод subList у list'а доставал фактически limit путей
         */
        int limit = Integer.parseInt(getParams().get(LIMIT_PARAM).get(0)) + 1;
        if (offset > routes.size()) {
            return Collections.emptyList();
        } else {
            if (offset + limit > routes.size()) {
                return routes.subList(offset, routes.size());
            } else {
                return routes.subList(offset, offset + limit);
            }
        }
    }

    private boolean checkParams(Map<String, List<String>> params) {
        boolean notNull = params.get(LIMIT_PARAM) != null &&
                params.get(LIMIT_PARAM).get(0) != null &&
                params.get(OFFSET_PARAM) != null &&
                params.get(OFFSET_PARAM).get(0) != null;
        return notNull && isNumeric(params.get(LIMIT_PARAM).get(0)) && isNumeric(params.get(OFFSET_PARAM).get(0));
    }
}
