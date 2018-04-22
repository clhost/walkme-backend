package io.walkme.mappers;

import io.walkme.graph.Node;
import io.walkme.response.route.ResponseRouteEntity;

public class NodeToRouteEntityMapper implements Mapper<ResponseRouteEntity, Node> {
    @Override
    public ResponseRouteEntity map(Node node) {
        return new ResponseRouteEntity(
                node.getPoint(),
                node.getName(),
                node.getCategory(),
                node.getCategoryId(),
                node.getAddress(),
                node.getAddressAdditional(),
                node.getWorkingTime());
    }
}
