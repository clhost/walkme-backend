package route.mappers;

import route.core.ResponseRouteEntity;
import route.graph.Node;

public class NodeToResponseRouteEntityMapper implements Mapper<ResponseRouteEntity, Node> {
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
