package io.walkme.graph;

import io.walkme.core.PlaceHolder;
import io.walkme.graph.prod.Node;
import io.walkme.mappers.Mapper;
import io.walkme.mappers.NodeToRouteEntityMapper;
import io.walkme.response.route.RouteEntity;
import io.walkme.services.GenericEntityService;
import io.walkme.services.PlaceService;
import io.walkme.services.fields.PlaceFields;
import io.walkme.storage.entities.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouteEntityProvider {
    private final Mapper<RouteEntity, Node> mapper = new NodeToRouteEntityMapper();

    public RouteEntity randomRouteEntity() {
        Random random = new Random();
        int p = random.nextInt(PlaceHolder.getAll().size());

        return mapper.map(PlaceHolder.getAll().get(p));
    }
}
