package io.walkme.graph.prod;

import io.walkme.graph.prod.GraphHopperRouteChecker;
import io.walkme.graph.prod.Route;
import io.walkme.graph.prod.RouteChecker;
import io.walkme.storage.entities.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by tFNiYaFF on 18.03.2018.
 */
public class Ways {

    private int[] ids;
    private final long startTime;
    private List<Node> resultPlaces;
    private List<Location> resultLocations;
    private List<List<Location>> allResultLocations;
    private Set<Location> alreadyUsed = new HashSet<>();


    private final int NEXT_STEP_POINTS_RANDOM_COUNT = 40;
    private final double MIN_DISTANCE_BETWEEN_TWO_POINTS = 650;//meters
    private final double MAX_DISTANCE_BETWEEN_TWO_POINTS = 1500; //meters
    private final double MAX_WALK_TIME = 240 * 1000 * 60; //mills
    private final int MAX_POINTS_PER_ONE_ROUTE = 5;
    private boolean RESET_TIME = false;
    private final int MAX_DISTANCE_OF_INTERSECTION = 70;
    private final int INTERSECTION_LIMIT = NEXT_STEP_POINTS_RANDOM_COUNT;

    private final Location CITY_CENTER_SPB = new Location(59.93d, 30.31d);
    private final Location USER_START_LOCATION;

    private static List<Node> nodes;
    private static final RouteChecker routeChecker = new GraphHopperRouteChecker();
    private static boolean routeCheckerIsRunning = false;

    public static boolean ghStart(){
        if(!routeCheckerIsRunning){
            routeChecker.start();
            routeCheckerIsRunning = true;
            return true;
        }
        else{
            System.out.println("GH ALREADY WORKS");
            return false;
        }
    }

    public static boolean initializePlaces(List<Node> places){
        if(nodes == null){
            nodes = places;
            return true;
        }
        else{
            System.out.println("ALREADY INITIALIZED");
            return false;
        }
    }


    public Ways(long startTime, Location startLocation, int[] ids) throws Exception {
        if(nodes == null){
            throw new Exception("NODES == NULL");
        }
        if(!routeCheckerIsRunning){
            throw new Exception("GH NOT STARTED");
        }
        this.ids = ids;
        resultPlaces = new ArrayList<>();
        resultLocations = new ArrayList<>();
        allResultLocations = new ArrayList<>();
        this.startTime = startTime;
        USER_START_LOCATION = startLocation;
    }

    public RouteHolder getWays() {
        System.out.println("**********************new way*******************");
        do {
            RESET_TIME = false;
            execute();
            if (RESET_TIME) reset();
        } while (RESET_TIME);
        System.out.println("*********************ready**********************");
        RouteHolder rh = new RouteHolder(allResultLocations, resultPlaces);
        return rh;
    }
    
    private void reset() {
        resultPlaces = new ArrayList<>();
        resultLocations = new ArrayList<>();
        alreadyUsed = new HashSet<>();
        allResultLocations = new ArrayList<>();
    }

    private void execute() {
        Node currentPoint = getStartPoint();
        if (currentPoint == null) {
            System.out.println("Start point is not available");
            return;
        }
        alreadyUsed.add(currentPoint.getPoint());
        resultPlaces.add(currentPoint);
        double summaryTime = 0;
        int iter = 0;
        while (true) {
            if (iter > INTERSECTION_LIMIT) {
                System.out.println("RESET");
                RESET_TIME = true;
                return;
            }
            ArrayList<Node> nextStep = new ArrayList<>(NEXT_STEP_POINTS_RANDOM_COUNT);
            for (int i = 0; i < NEXT_STEP_POINTS_RANDOM_COUNT; i++) {
                int rnd;
                do {
                    rnd = ((int) (Math.random() * nodes.size() * 482992)) % nodes.size();
                } while (alreadyUsed.contains(nodes.get(rnd).getPoint()));
                nextStep.add(nodes.get(rnd));
            }
            Node tmpPoint = getNearestPoint(currentPoint.getPoint(), nextStep);
            if (tmpPoint == null) continue;
            List<Location> allLocations = getAllPoints(currentPoint.getPoint(), tmpPoint.getPoint());
            alreadyUsed.add(tmpPoint.getPoint());
            if (checkIntersection(allLocations)) {
                ++iter;
                continue;
            }
            summaryTime += getTime(currentPoint.getPoint(), tmpPoint.getPoint());
            resultLocations.addAll(allLocations);
            allResultLocations.add(allLocations);
            currentPoint = tmpPoint;
            resultPlaces.add(currentPoint);
            if (summaryTime >= MAX_WALK_TIME || resultPlaces.size() >= MAX_POINTS_PER_ONE_ROUTE) break;
        }
    }

    private Node getStartPoint() {
        if (USER_START_LOCATION == null)
            return getNearestPoint(CITY_CENTER_SPB, nodes);
        else
            return getNearestPoint(USER_START_LOCATION, nodes);
    }

    private Node getNearestPoint(Location from, List<Node> set) {
        Node resultPoint = null;
        double minDistance = Double.MAX_VALUE;
        for (Node aSet : set) {
            double currentDistance = 0;
            currentDistance = Math.sqrt((from.getLat() - aSet.getPoint().getLat()) * (from.getLat() - aSet.getPoint().getLat()) + (aSet.getPoint().getLng() - from.getLng()) * (aSet.getPoint().getLng() - from.getLng())); //getDistance(from, set.get(i).getPoint());
            if (currentDistance < minDistance) {
                double realDistance = getDistance(from, aSet.getPoint());
            if (realDistance > MIN_DISTANCE_BETWEEN_TWO_POINTS && realDistance < MAX_DISTANCE_BETWEEN_TWO_POINTS) {
                    minDistance = currentDistance;
                    resultPoint = aSet;
                }
            }
        }
        return resultPoint;
    }

    private double getDistance(Location from, Location to) {
        double lat1 = from.getLat();
        double lon1 = from.getLng();
        double lat2 = to.getLat();
        double lon2 = to.getLng();
        int r = 6371;
        double dLat = deg2rad(lat2 - lat1);
        double dLon = deg2rad(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c * 1000;
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    private double getTime(Location from, Location to) {
        Route route = routeChecker.getWalkingRoute(from.getLat(), from.getLng(), to.getLat(), to.getLng());
        return route.getTime();
    }

    private List<Location> getAllPoints(Location from, Location to) {
        Route route = routeChecker.getWalkingRoute(from.getLat(), from.getLng(), to.getLat(), to.getLng());
        return route.getPointList();
    }

     private boolean checkIntersection(List<Location> points) {
        List<Location> reversePoints = new ArrayList<>();
        reversePoints.addAll(points);
        for (int i = 0; i < reversePoints.size() / 2; i++) {
            Location tmp = reversePoints.get(i);
            reversePoints.set(i, reversePoints.get(reversePoints.size() - i - 1));
            reversePoints.set(reversePoints.size() - i - 1, tmp);
        }
        for (int i = 0; i < resultLocations.size(); i++) {
            for (int j = 0; j < points.size(); j++) {
                if (compareLoc(resultLocations.get(i), points.get(j))) {
                    int size = getSizeOfIntersection(i, j, points);
                    double distance = getDistanceOfIntersection(j, size, points);
                    if (distance > MAX_DISTANCE_OF_INTERSECTION) {
                        System.out.println("interp: " + distance);
                        return true;
                    }
                    j += size;
                }
            }
            for (int j = 0; j < reversePoints.size(); j++) {
                if (compareLoc(resultLocations.get(i), reversePoints.get(j))) {
                    int size = getSizeOfIntersection(i, j, reversePoints);
                    double distance = getDistanceOfIntersection(j, size, reversePoints);
                    if (distance > MAX_DISTANCE_OF_INTERSECTION) {
                        System.out.println("rev: " + distance);
                        return true;
                    }
                    j += size;
                }
            }
        }
        return false;
    }

    private boolean compareLoc(Location f, Location s) {
        return f.getLat() == s.getLat() && f.getLng() == s.getLng();
    }

    private int getSizeOfIntersection(int allStart, int pointsStart, List<Location> points) {
        int size = 0;
        int i = allStart;
        int j = pointsStart;
        while (i < resultLocations.size() && j < points.size() && compareLoc(resultLocations.get(i++), points.get(j++)))
            ++size;
        return size;
    }

    private double getDistanceOfIntersection(int pointsStart, int size, List<Location> points) {
        double distance = 0;
        int i = pointsStart;
        for (int k = 0; k < size - 1; k++) {
            distance += getDistance(points.get(i++), points.get(i));
        }
        return distance;
    }
}
