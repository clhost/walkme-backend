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


    private final int NEXT_STEP_POINTS_RANDOM_COUNT = 30;
    private final double MIN_DISTANCE_BETWEEN_TWO_POINTS = 650;//meters
    private final double MAX_DISTANCE_BETWEEN_TWO_POINTS = 1250; //meters
    private final double MAX_WALK_TIME = 240 * 1000 * 60; //mills
    private final int MAX_POINTS_PER_ONE_ROUTE = 5;
    private final int MAX_DISTANCE_OF_INTERSECTION = 70;

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
        execute();
        RouteHolder rh = new RouteHolder(allResultLocations, resultPlaces);
        return rh;
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
        while (true) {
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
            summaryTime += getTime(currentPoint.getPoint(), tmpPoint.getPoint());
            List<Location> allLocations = getAllPoints(currentPoint.getPoint(), tmpPoint.getPoint());
            alreadyUsed.add(tmpPoint.getPoint());
            if (checkIntersection(allLocations)) continue;
            resultLocations.addAll(allLocations);
            allResultLocations.add(allLocations);
            currentPoint = tmpPoint;
            resultPlaces.add(currentPoint);
            if (summaryTime >= MAX_WALK_TIME || resultPlaces.size() >= MAX_POINTS_PER_ONE_ROUTE) break;
            //System.out.println("Point: " + currentPoint.getPoint().getLng() + " " + currentPoint.getPoint().getLat()
                    //+ " Time: " + summaryTime);
        }
    }

    private boolean compareLocations(List<Location> newList) {
        int k = 0;
        for (int i = 0; i < newList.size(); i++) {
            for (int j = 0; j < resultLocations.size(); j++) {
                if (newList.get(i).getLat() == resultLocations.get(j).getLat() && newList.get(i).getLng() == resultLocations.get(j).getLng()) {
                    k++;
                    if (k > 4) return false;
                }
            }
        }
        System.out.println("Comparing ok");
        return true;
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
                //System.out.println("Point: "+from.getLng() + " " + from.getLat() + " " + aSet.getPoint().getLng() + " " +aSet.getPoint().getLat()+ " Real distance: " + realDistance);
                if (realDistance > MIN_DISTANCE_BETWEEN_TWO_POINTS && realDistance < MAX_DISTANCE_BETWEEN_TWO_POINTS) {
                    minDistance = currentDistance;
                    resultPoint = aSet;
                    //System.out.println("Distance is ok");
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
                double distanceOfIntersection = 0;
                int pStart = j;
                int rpStart = j;
                int pFinish = 0;
                int rpFinish = 0;
                int ik = 0;
                int jk = 0;
                while (jk + j < points.size() && ik + i < resultLocations.size() && ((points.get(j + jk).getLng() == resultLocations.get(i + ik).getLng() && points.get(j + jk).getLat() == resultLocations.get(i + ik).getLat()) || ((reversePoints.get(j + jk).getLng() == resultLocations.get(i + ik).getLng() && reversePoints.get(j + jk).getLat() == resultLocations.get(i + ik).getLat())))) {
                    if ((points.get(j + jk).getLng() == resultLocations.get(i + ik).getLng() && points.get(j + jk).getLat() == resultLocations.get(i + ik).getLat())) {
                        pFinish++;
                    }
                    if (((reversePoints.get(jk).getLng() == resultLocations.get(i + ik).getLng() && reversePoints.get(j + jk).getLat() == resultLocations.get(i + ik).getLat()))) {
                        rpFinish++;
                    }
                    jk++;
                    ik++;
                }
                if (pFinish > rpFinish) {
                    for (int u = pStart; u < pFinish - 1; u++) {
                        distanceOfIntersection += getDistance(points.get(u), points.get(u + 1));
                    }
                } else {
                    for (int u = rpStart; u < rpFinish - 1; u++) {
                        distanceOfIntersection += getDistance(reversePoints.get(u), reversePoints.get(u + 1));
                    }
                }
                if (distanceOfIntersection > MAX_DISTANCE_OF_INTERSECTION) {
                    return true;
                }
            }
        }
        //System.out.println("ALL OK");
        return false;
    }
}
