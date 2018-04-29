package route.graph;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.shapes.BBox;

public class GraphHopperRouteChecker implements RouteChecker {
    private static final String GRAPH_LOC = "target/GraphHopper";
    private LocationIndex locationIndex;
    private EdgeFilter footDefaultEdgeFilter;
    private BBox mapBbox;
    private GraphHopper instance;

    GraphHopperRouteChecker() {
        instance = new GraphHopperOSM()
                .setStoreOnFlush(false)
                .forServer() // decrease startup latency
                .setEncodingManager(new EncodingManager("foot"))
                .setCHEnabled(false)
                .setGraphHopperLocation(GRAPH_LOC)
                .setDataReaderFile("maps/RU-SPE.osm.pbf");
    }

    @Override
    public void start() {
        instance.importOrLoad();
    }

    @Override
    public Route getWalkingRoute(double fromLat, double fromLon, double toLat, double toLon) {
        GHResponse grsp = instance.route(new GHRequest(fromLat, fromLon, toLat, toLon).setVehicle("foot"));
        return new GraphHooperRoute(grsp);
    }

    @Override
    public boolean isPointValid(double lat, double lon) {
        if (!mapBbox.contains(lat, lon)){
            return false;
        }

        QueryResult queryResult = locationIndex.findClosest(lat, lon, footDefaultEdgeFilter);
        return queryResult.isValid();
    }
}
