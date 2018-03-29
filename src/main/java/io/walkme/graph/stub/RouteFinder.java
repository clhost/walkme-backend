package io.walkme.graph.stub;

import com.google.common.util.concurrent.AtomicDouble;
import io.walkme.storage.entities.Location;
import io.walkme.storage.entities.Place;

import java.util.*;

public class RouteFinder {
    private static final RouteChecker routeChecker = new GraphHopperRouteChecker();
    private static AtomicDouble jvmHeatCoefficient = new AtomicDouble(0.000012);
    private static final double JVM_HEAT_LIMIT = 100;
    private static boolean isStart = false;

    private static RouteFinder instance;

    public static RouteFinder getInstance() {
        RouteFinder result = instance;
        if (result == null) {
            synchronized (RouteFinder.class) {
                result = instance;
                if (result == null) {
                    result = new RouteFinder();
                    instance = result;
                }
            }
        }
        return result;
    }

    public static void init() {
        getInstance();
    }

    /**
     * Use 2 points
     * Use map as 2d vector
     */
    public List<Location> findRandomPath(Place p1, Place p2) {
        Route route = routeChecker.getWalkingRoute(
                p1.getLocation().getLat(),
                p1.getLocation().getLng(),
                p2.getLocation().getLat(),
                p2.getLocation().getLng());

        return route.getPoints();
    }

    private RouteFinder() {
        start();
        heat();
    }

    private void start() {
        System.out.println("Starting route checker...");
        routeChecker.start();
        isStart = true;
    }

    private void check() {
        if (!isStart) {
            throw new IllegalStateException("Was the RouterFinder started?");
        }
    }

    private void heat() {
        check();
        for (int i = 0; i < JVM_HEAT_LIMIT; i++, jvmHeatCoefficient.set(jvmHeatCoefficient.doubleValue() + 0.00007)) {
            System.out.print("\rJVM heating:" + (i + 1) + "%");
            routeChecker
                    .getWalkingRoute(
                            59.999593 + jvmHeatCoefficient.doubleValue(),
                            30.36647 + jvmHeatCoefficient.doubleValue(),
                            59.923870 + jvmHeatCoefficient.doubleValue(),
                            30.386808 + jvmHeatCoefficient.doubleValue());
        }

        System.out.println();
        jvmHeatCoefficient.set(0.000012);
    }
}
