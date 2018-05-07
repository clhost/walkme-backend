package route.graph;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.DefaultEdgeFilter;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.shapes.BBox;

public class GraphHopperRouteChecker implements RouteChecker {
    private LocationIndex locationIndex;
    private EdgeFilter footDefaultEdgeFilter;
    private BBox mapBbox;
    private GraphHopper instance;

    public GraphHopperRouteChecker(String mapPath) {
        EncodingManager encodingManager = new EncodingManager("foot");
        footDefaultEdgeFilter = new DefaultEdgeFilter(encodingManager.getEncoder("foot"));
        String graphLoc = "gh_target/GraphHopper-" + mapPath.substring(mapPath.lastIndexOf("/") + 1);
        instance = new GraphHopperOSM()
                .setStoreOnFlush(false)
                .forServer() // decrease startup latency
                .setEncodingManager(new EncodingManager("foot"))
                .setCHEnabled(false)
                .setGraphHopperLocation(graphLoc)
                .setDataReaderFile(mapPath);
    }

    @Override
    public void start() {
        instance.importOrLoad();
        locationIndex = instance.getLocationIndex();
        mapBbox = instance.getGraphHopperStorage().getBounds();
    }

    @Override
    public Route getWalkingRoute(double fromLat, double fromLon, double toLat, double toLon) {
        GHResponse grsp = instance.route(new GHRequest(fromLat, fromLon, toLat, toLon).setVehicle("foot"));
        return new GraphHooperRoute(grsp);
    }

    @Override
    public boolean isPointValid(double lat, double lon) {
        if (!mapBbox.contains(lat, lon)) {
            return false;
        }
        QueryResult queryResult = locationIndex.findClosest(lat, lon, footDefaultEdgeFilter);
        return queryResult.isValid();
    }
}
