package io.walkme.handlers.route;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.walkme.core.GlobalProps;
import io.walkme.graph.PlaceProvider;
import io.walkme.graph.stub.RouteFinder;
import io.walkme.response.route.RouteBuilder;
import io.walkme.response.route.RouteEntity;
import io.walkme.services.GenericEntityService;
import io.walkme.services.PlaceService;
import io.walkme.services.SessionService;
import io.walkme.storage.entities.*;
import io.walkme.utils.ResponseBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * handle: /api/getRoute
 * params (json): token, lat, lng, categories
 *
 *
 */
public class GetRouteHandler extends ChannelInboundHandlerAdapter {
    private static final String API_PREFIX = "api";
    private static final String API_GET_ROUTE = "getRoute";

    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_LAT = "lat";
    private static final String PARAM_LNG = "lng";
    private static final String PARAM_CATEGORIES = "categories";

    private static final GenericEntityService<Place, String> placeService = new PlaceService();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            ctx.fireChannelRead(msg);
            return;
        }

        FullHttpRequest request = (FullHttpRequest) msg;

        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        String[] tokens = decoder.path().substring(1).split("/");
        Map<String, List<String>> params = decoder.parameters();


        if (tokens.length < 2) {
            ctx.fireChannelRead(msg);
        } else if (tokens[0].equals(API_PREFIX) && tokens[1].equals(API_GET_ROUTE)){
            handleRoute(ctx, params);
        }
    }

    private void handleRoute(ChannelHandlerContext ctx, Map<String, List<String>> params) throws Exception {
        if (GlobalProps.isStub) {
            Place p1 = PlaceProvider.randomPlace();
            Place p2 = PlaceProvider.randomPlace();

            List<Location> points = RouteFinder.getInstance().findRandomPath(p1, p2);

            List<RouteEntity> entities = new ArrayList<>();
            entities.add(RouteEntity.of(p1));
            entities.add(RouteEntity.of(p2));

            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.OK, RouteBuilder.asJson(200, entities, points)));
            ctx.close();
        } else {
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.OK, ResponseBuilder.JSON_STUB_BAD_REQUEST));
            ctx.close();
        }
    }
}
