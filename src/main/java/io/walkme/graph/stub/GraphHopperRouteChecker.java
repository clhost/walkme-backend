package io.walkme.graph.stub;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;

public class GraphHopperRouteChecker implements RouteChecker {
    private static final String GRAPH_LOC = "target/GraphHopper";
    private GraphHopper instance;

    public GraphHopperRouteChecker(){
        instance = new GraphHopperOSM()
                .setStoreOnFlush(false)
                .setEncodingManager(new EncodingManager("foot"))
                .setCHEnabled(false)
                .setGraphHopperLocation(GRAPH_LOC)
                .setDataReaderFile("maps/RU-SPE.osm.pbf");
    }

    @Override
    public void start(){
        instance.importOrLoad();
    }

    @Override
    public Route getWalkingRoute(double fromLat, double fromLon, double toLat, double toLon) {
        GHResponse resp = instance.route(new GHRequest(fromLat, fromLon, toLat, toLon).setVehicle("foot"));
        return new GraphHooperRoute(resp);
    }
}