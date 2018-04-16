package io.walkme.graph;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;

public class GraphHopperRouteChecker implements RouteChecker {
    private static final String GRAPH_LOC = "target/GraphHopper";

    private GraphHopper instance;

    public GraphHopperRouteChecker() {
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
}
