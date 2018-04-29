package route.graph;

public interface RouteChecker {
    void start();
    boolean isPointValid(double lat, double lon);
    Route getWalkingRoute(double fromLat, double fromLon, double toLat, double toLon);
}
