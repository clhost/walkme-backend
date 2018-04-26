package route.graph;

public interface RouteChecker {
    void start();

    Route getWalkingRoute(double fromLat, double fromLon, double toLat, double toLon);
}
