package io.walkme.mappers;

import io.walkme.graph.prod.Node;
import io.walkme.response.route.RouteEntity;

public class NodeToRouteEntityMapper implements Mapper<RouteEntity, Node> {
    @Override
    public RouteEntity map(Node node) {
        return new RouteEntity(
                node.getPoint(),
                node.getName(),
                node.getCategory(),
                node.getCategoryId(),
                node.getAddress(),
                node.getAddressAdditional(),
                node.getWorkingTime());
    }
}
