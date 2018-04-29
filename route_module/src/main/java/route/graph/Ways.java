package route.graph;

import route.graph.exceptions.NotEnoughPointsException;
import route.graph.exceptions.NotInitializedException;
import route.graph.exceptions.StartPointIsNotAvailableException;
import route.storage.entities.Day;
import route.storage.entities.Location;
import route.storage.entities.Schedule;
import route.storage.entities.ScheduleTime;

import java.util.*;

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

    private final int NUMBER_OF_CULTURE_POINTS = 3;
    private final int NEXT_STEP_POINTS_RANDOM_COUNT = 40;
    private final double MIN_DISTANCE_BETWEEN_TWO_POINTS = 500;//meters
    private double MAX_DISTANCE_BETWEEN_TWO_POINTS = 1300; //meters
    private final double MAX_WALK_TIME = 240 * 1000 * 60; //mills
    private final int MAX_POINTS_PER_ONE_ROUTE = 5;
    private final int MIN_POINTS_PER_ONE_ROUTE = 3;
    private final int RADIUS_INCREMENT = 100;
    private boolean RESET_TIME = false;
    private final int MAX_DISTANCE_OF_INTERSECTION = 25;
    private final int INTERSECTION_LIMIT = 20;

    private final Location CITY_CENTER_SPB = new Location(59.93d, 30.31d);
    private final Location USER_START_LOCATION;

    private static List<Node> inputNodes;
    private List<Node> nodes;
    private static List<Node> cultureNodes = new ArrayList<>();
    private static final RouteChecker routeChecker = new GraphHopperRouteChecker();
    private static boolean routeCheckerIsRunning = false;
    private long currentTime;


    public static void ghStart() {
        if (!routeCheckerIsRunning) {
            routeChecker.start();
            routeCheckerIsRunning = true;
        } else {
            System.out.println("GH ALREADY WORKS");
        }
    }

    public static void initializePlaces(List<Node> places) {
        if (inputNodes == null) {
            inputNodes = places;
        } else {
            System.out.println("ALREADY INITIALIZED");
        }
    }


    public Ways(long startTime, Location startLocation, int[] ids) throws NotInitializedException {
        if (inputNodes == null || !routeCheckerIsRunning) {
            throw new NotInitializedException();
        }
        this.ids = ids;
        resultPlaces = new ArrayList<>();
        resultLocations = new ArrayList<>();
        allResultLocations = new ArrayList<>();
        nodes = new ArrayList<>();
        this.startTime = startTime;
        currentTime = startTime;
        USER_START_LOCATION = startLocation;
    }

    public RouteHolder getWays() throws StartPointIsNotAvailableException, NotEnoughPointsException {
        System.out.println("**********************new way********************");
        filterNodes();
        do {
            RESET_TIME = false;
            execute();
            if (RESET_TIME) reset();
        } while (RESET_TIME);
        System.out.println("***********************ready*********************");
        if (resultPlaces.size() < MIN_POINTS_PER_ONE_ROUTE) throw new NotEnoughPointsException();
        return new RouteHolder(allResultLocations, resultPlaces);
    }

    private void reset() {
        resultPlaces = new ArrayList<>();
        resultLocations = new ArrayList<>();
        alreadyUsed = new HashSet<>();
        allResultLocations = new ArrayList<>();
    }

    private void execute() throws StartPointIsNotAvailableException {
        Node currentPoint = getStartPoint();
        if (currentPoint == null) {
            throw new StartPointIsNotAvailableException();
        }
        alreadyUsed.add(currentPoint.getPoint());
        resultPlaces.add(currentPoint);
        double summaryTime = 0;
        int iteration = 0;
        while (true) {
            if (iteration > INTERSECTION_LIMIT) {
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
            if (tmpPoint == null) {
                MAX_DISTANCE_BETWEEN_TWO_POINTS += RADIUS_INCREMENT;
                continue;
            }
            List<Location> allLocations = new ArrayList<>();
            try {
                allLocations = getAllPoints(currentPoint.getPoint(), tmpPoint.getPoint());
            }catch(Exception e){
                System.out.println("INVALID POINT");
                continue;
            }
            alreadyUsed.add(tmpPoint.getPoint());
            if (checkIntersection(allLocations)) {
                ++iteration;
                continue;
            }
            double time = getTime(currentPoint.getPoint(), tmpPoint.getPoint());
            summaryTime += time;
            currentTime += time/1000; //в секунды
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

    private boolean isTimeOk(Schedule schedule){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        int dayNow = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayNow == 1) dayNow = 7; else dayNow = (dayNow+6)%7;
        ArrayList<ScheduleTime> st = (ArrayList<ScheduleTime>)schedule.getScheduleInfo().get(Day.getByDayOfWeek(dayNow));
        boolean isOk = false;
        for (ScheduleTime aSt : st) {
            if (aSt.getStart() == -1 || aSt.getFinish() == -1){
                return false;
            }
            if(aSt.getStart() < calendar.get(Calendar.HOUR_OF_DAY)*3600 && aSt.getFinish() > calendar.get(Calendar.HOUR_OF_DAY)*3600) isOk = true;
        }
        return isOk;
    }

    private Node getNearestPoint(Location from, List<Node> set) {
        Node resultPoint = null;
        double minDistance = Double.MAX_VALUE;
        for (Node aSet : set) {
            if(!isTimeOk(aSet.getSchedule())) continue;
            double currentDistance = getDistance(from, aSet.getPoint());
            if (currentDistance < minDistance && currentDistance > MIN_DISTANCE_BETWEEN_TWO_POINTS && currentDistance < MAX_DISTANCE_BETWEEN_TWO_POINTS) {
                System.out.println("Point: "+aSet.getPoint().getLat() + " "+ aSet.getPoint().getLng());
                //System.out.println("Schedule: " + Arrays.toString(aSet.getSchedule().getScheduleInfo().get(Day.getByDayOfWeek(7)).toArray()));
                minDistance = currentDistance;
                resultPoint = aSet;
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
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
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

    private void filterNodes() {
        for (Node inputNode : inputNodes) {
            if (inputNode.getCategoryId() == 4 || inputNode.getCategoryId() == 5) {
                cultureNodes.add(inputNode);
            }
        }
        if (ids == null || ids.length == 0) {
            nodes = inputNodes;
            return;
        }
        for (Node inputNode : inputNodes) {
            for (int id : ids) {
                if (inputNode.getCategoryId() == id) {
                    nodes.add(inputNode);
                    break;
                }
            }
        }
    }
}
