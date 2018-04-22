package io.walkme.handlers.route;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.walkme.graph.Node;
import io.walkme.graph.RouteHolder;
import io.walkme.graph.Ways;
import io.walkme.graph.exceptions.NotEnoughPointsException;
import io.walkme.graph.exceptions.StartPointIsNotAvailableException;
import io.walkme.handlers.BaseHttpHandler;
import io.walkme.mappers.Mapper;
import io.walkme.mappers.NodeToResponseRouteEntityMapper;
import io.walkme.response.route.ResponseRouteEntity;
import io.walkme.response.route.RouteBuilder;
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
 */
public class GetRouteHandler extends BaseHttpHandler {
    private static final String PARAM_LAT = "lat";
    private static final String PARAM_LNG = "lng";
    private static final String PARAM_CATEGORIES = "categories";

    private final Logger logger = LogManager.getLogger(GetRouteHandler.class);
    private final Mapper<ResponseRouteEntity, Node> mapper = new NodeToResponseRouteEntityMapper();

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
            Ways finder = new Ways(
                    System.currentTimeMillis(),
                    new Location(
                            Double.parseDouble(params.get(PARAM_LAT).get(0)),
                            Double.parseDouble(params.get(PARAM_LNG).get(0))),
                    new int[]{});


            RouteHolder holder = finder.getWays();
            List<ResponseRouteEntity> entities = new ArrayList<>();

            for (Node node : holder.getPlaces()) {
                entities.add(mapper.map(node));
            }

            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.OK, RouteBuilder.asJson(200, entities, holder.getPoints())));
        } catch (StartPointIsNotAvailableException e) {
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    ResponseBuilder.JSON_START_POINT_UNAVAILABLE_RESPONSE
            ));
        } catch (NotEnoughPointsException e) {
            ctx.writeAndFlush(ResponseBuilder.buildJsonResponse(
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    ResponseBuilder.JSON_NOT_ENOUGH_POINTS_RESPONSE
            ));
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
        ctx.close();
        release();
    }
}
