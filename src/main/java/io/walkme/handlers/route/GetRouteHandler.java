package io.walkme.handlers.route;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.walkme.core.GlobalProps;
import io.walkme.graph.PlaceProvider;
import io.walkme.graph.stub.RouteFinder;
import io.walkme.handlers.BaseHttpHandler;
import io.walkme.response.route.RouteBuilder;
import io.walkme.response.route.RouteEntity;
import io.walkme.storage.entities.*;
import io.walkme.utils.ResponseBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * handle: /api/getRoute
 * params (json): token, lat, lng, categories
 *
 *
 */
public class GetRouteHandler extends BaseHttpHandler {
    private static final String API_PREFIX = "api";
    private static final String API_GET_ROUTE = "getRoute";

    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_LAT = "lat";
    private static final String PARAM_LNG = "lng";
    private static final String PARAM_CATEGORIES = "categories";

    private final Logger logger = LogManager.getLogger(GetRouteHandler.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!check(msg)) {
            ctx.fireChannelRead(msg);
            return;
        }

        hold((FullHttpRequest) msg);

        String[] tokens = getTokens();
        Map<String, List<String>> params = getParams();


        if (tokens.length < 2) {
            ctx.fireChannelRead(msg);
        } else if (tokens[0].equals(API_PREFIX) && tokens[1].equals(API_GET_ROUTE)){
            handleRoute(ctx, params);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void handleRoute(ChannelHandlerContext ctx, Map<String, List<String>> params) throws Exception {
        if (GlobalProps.isStub) {
            Place p1 = PlaceProvider.get0();//.randomPlace();
            Place p2 = PlaceProvider.get1();//.randomPlace();

            List<Location> points = RouteFinder.getInstance().findRandomPath(p1, p2);

            List<RouteEntity> entities = new ArrayList<>();
            entities.add(RouteEntity.of(p1));
            entities.add(RouteEntity.of(p2));

            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.OK, RouteBuilder.asJson(200, entities, points)));
            ctx.close();
        } else {
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.OK, ResponseBuilder.JSON_STUB_BAD_RESPONSE));
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage());
        cause.printStackTrace();
    }
}
