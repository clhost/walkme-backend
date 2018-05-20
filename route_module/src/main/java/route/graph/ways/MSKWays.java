package route.graph.ways;

import net.jafama.FastMath;
import route.graph.*;
import route.graph.exceptions.NotEnoughPointsException;
import route.graph.exceptions.NotInitializedException;
import route.graph.exceptions.StartPointIsNotAvailableException;
import route.storage.entities.Day;
import route.storage.entities.Location;
import route.storage.entities.Schedule;
import route.storage.entities.ScheduleTime;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MSKWays {
    private int[] ids;
    private final long startTime;
    private List<Node> resultPlaces;
    private List<Location> resultLocations;
    private List<List<Location>> allResultLocations;
    private Set<Location> alreadyUsed = new HashSet<>();
    private Set<Location> alreadyUsedThisIteration = new HashSet<>();

    private final int NEXT_STEP_POINTS_RANDOM_COUNT = 8;
    private final double MIN_DISTANCE_BETWEEN_TWO_POINTS = 500;//meters
    private double MAX_DISTANCE_BETWEEN_TWO_POINTS = 1300; //meters
    private final double MAX_WALK_TIME = 240 * 1000 * 60; //mills
    private final int MAX_POINTS_PER_ONE_ROUTE = 5;
    private final int MIN_POINTS_PER_ONE_ROUTE = 3;
    private final int RADIUS_INCREMENT = 400;
    private boolean RESET_TIME = false;
    private final int MAX_DISTANCE_OF_INTERSECTION = 150;
    private static final int MAX_CATEGORIES_NUMBER = 5;
    private static final int MAX_RESET_TIMES = 3;
    private int currentResets = 0;

    private final Location USER_START_LOCATION;
    private int[] categoryDistribution = new int[MAX_CATEGORIES_NUMBER];
    private static List<Node>[] nodesByCategories = new List[MAX_CATEGORIES_NUMBER + 1];
    private static final RouteChecker routeChecker = new GraphHopperRouteChecker("maps/RU-MOW.osm.pbf");
    private static boolean routeCheckerIsRunning = false;
    private static boolean nodesIsInitialized = false;
    private long currentTime;


    public static void ghStart() {
        if (!routeCheckerIsRunning) {
            routeChecker.start();
            routeCheckerIsRunning = true;
        } else {
            System.out.println("Graph hooper already works");
        }
    }

    public static void initializePlaces(List<Node> places) {
        if (nodesIsInitialized) {
            System.out.println("Nodes already initialized!");
            return;
        }
        for (int i = 1; i <= MAX_CATEGORIES_NUMBER; i++) {
            nodesByCategories[i] = new ArrayList<>();
        }
        for (Node inputNode : places) {
            nodesByCategories[inputNode.getCategoryId()].add(inputNode);
        }
        nodesIsInitialized = true;
    }

    public static boolean isPointValid(double lat, double lon) {
        if (!routeCheckerIsRunning) {
            throw new IllegalStateException("Service must be started.");
        }
        return routeChecker.isPointValid(lat, lon);
    }

    public MSKWays(long startTime, Location startLocation, int[] ids) throws NotInitializedException {
        if (!routeCheckerIsRunning || !nodesIsInitialized) {
            throw new NotInitializedException();
        }
        this.ids = ids;
        resultPlaces = new ArrayList<>();
        resultLocations = new ArrayList<>();
        allResultLocations = new ArrayList<>();
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
            ++currentResets;
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

    private void execute() throws StartPointIsNotAvailableException, NotEnoughPointsException {
        Node currentPoint = null;
        int iteration = 0;
        double summaryTime = 0;
        List<Node> nodes1;
        while (true) {
            if(currentResets == 10) throw new NotEnoughPointsException();
            nodes1 = nodesByCategories[categoryDistribution[iteration]];
            Location currentLocation = currentPoint == null ? USER_START_LOCATION : currentPoint.getPoint();
            ArrayList<Node> nextStep = new ArrayList<>(NEXT_STEP_POINTS_RANDOM_COUNT);
            alreadyUsedThisIteration = new HashSet<>();
            int real = 0;
            for (int i = 0; i < NEXT_STEP_POINTS_RANDOM_COUNT; i++) {
                Node nearestPoint = getNearestPoint(currentLocation, nodes1);
                if (nearestPoint == null) {
                    MAX_DISTANCE_BETWEEN_TWO_POINTS += RADIUS_INCREMENT;
                    continue;
                }
                nextStep.add(nearestPoint);
                alreadyUsedThisIteration.add(nearestPoint.getPoint());
                ++real;
            }
            ArrayList<Node> nextStepChecked = new ArrayList<>(NEXT_STEP_POINTS_RANDOM_COUNT);
            List<Location> allLocations = null;
            for (int i = 0; i < real; i++) {
                int rnd = ThreadLocalRandom.current().nextInt(0, nextStep.size());
                allLocations = getAllPoints(currentLocation, nextStep.get(rnd).getPoint());
                if (currentResets != MAX_RESET_TIMES && checkIntersection(allLocations)) continue;
                nextStepChecked.add(nextStep.get(rnd));
                break;
            }
            if (nextStepChecked.size() == 0) {
                MAX_DISTANCE_BETWEEN_TWO_POINTS += RADIUS_INCREMENT;
                System.out.println("RESET");
                RESET_TIME = true;
                return;
            }
            Node tempPoint = nextStepChecked.get(ThreadLocalRandom.current().nextInt(0, nextStepChecked.size()));
            allLocations = getAllPoints(currentLocation, tempPoint.getPoint());
            double time = getTime(currentLocation, tempPoint.getPoint());
            summaryTime += time;
            currentTime += time / 1000; //в секунды
            resultLocations.addAll(allLocations);
            allResultLocations.add(allLocations);
            alreadyUsed.add(tempPoint.getPoint());
            currentPoint = tempPoint;
            resultPlaces.add(currentPoint);
            ++iteration;
            if (summaryTime >= MAX_WALK_TIME || resultPlaces.size() >= MAX_POINTS_PER_ONE_ROUTE) break;
        }
    }

    private boolean isTimeOk(Schedule schedule) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        int dayNow = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayNow == 1) dayNow = 7;
        else dayNow = (dayNow + 6) % 7;
        ArrayList<ScheduleTime> st = (ArrayList<ScheduleTime>) schedule.getScheduleInfo().get(Day.getByDayOfWeek(dayNow));
        boolean isOk = false;
        for (ScheduleTime aSt : st) {
            if (aSt.getStart() == -1 || aSt.getFinish() == -1) {
                return false;
            }
            if (aSt.getStart() < calendar.get(Calendar.HOUR_OF_DAY) * 3600 && aSt.getFinish() > calendar.get(Calendar.HOUR_OF_DAY) * 3600)
                isOk = true;
        }
        return isOk;
    }

    private Node getNearestPoint(Location from, List<Node> set) {
        Node resultPoint = null;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < set.size(); i++) {
            double currentDistance = getDistance(from, set.get(i).getPoint());
            if (currentDistance < MIN_DISTANCE_BETWEEN_TWO_POINTS || currentDistance > MAX_DISTANCE_BETWEEN_TWO_POINTS || currentDistance >= minDistance)
                continue;
            if (!isTimeOk(set.get(i).getSchedule()) || alreadyUsed.contains(set.get(i).getPoint()) || alreadyUsedThisIteration.contains(set.get(i).getPoint()))
                continue;
            minDistance = currentDistance;
            resultPoint = set.get(i);
            if (minDistance < MIN_DISTANCE_BETWEEN_TWO_POINTS * 1.5) break;
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
        double a = FastMath.sin(dLat / 2) * FastMath.sin(dLat / 2) + FastMath.cos(deg2rad(lat1)) * FastMath.cos(deg2rad(lat2)) *
                FastMath.sin(dLon / 2) * FastMath.sin(dLon / 2);
        double c = 2 * FastMath.atan2(FastMath.sqrt(a), FastMath.sqrt(1 - a));
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
        if (ids == null || ids.length == 0 || ids.length == MAX_CATEGORIES_NUMBER) {
            for (int i = 0; i < MAX_CATEGORIES_NUMBER; i++) {
                categoryDistribution[i] = i + 1;
            }
        } else {
            Arrays.sort(ids);
            switch (ids.length) {
                case 1:
                    for (int i = 0; i < MAX_CATEGORIES_NUMBER; i++) categoryDistribution[i] = ids[0];
                    break;
                case 2:
                    categoryDistribution[0] = ids[0];
                    categoryDistribution[1] = ids[1];
                    if ((ids[1] != 4 && ids[1] != 5) || (ids[0] == 4 && ids[1] == 5)) {
                        for (int i = 2; i < MAX_CATEGORIES_NUMBER; i++)
                            if (Math.random() > 0.5)
                                categoryDistribution[i] = ids[0];
                            else
                                categoryDistribution[i] = ids[1];
                    } else {
                        for (int i = 2; i < MAX_CATEGORIES_NUMBER; i++) categoryDistribution[i] = ids[1];
                    }
                    break;
                case 3:
                    categoryDistribution[0] = ids[0];
                    categoryDistribution[1] = ids[1];
                    categoryDistribution[2] = ids[2];
                    if (ids[1] == 4) {
                        categoryDistribution[3] = ids[1];
                        categoryDistribution[4] = ids[2];
                    } else if (ids[2] == 5) {
                        categoryDistribution[3] = ids[2];
                        categoryDistribution[4] = ids[2];
                    } else {
                        categoryDistribution[3] = ids[1];
                        categoryDistribution[4] = ids[2];
                    }
                    break;
                case 4:
                    categoryDistribution[0] = ids[3]; //last category [parks or culture]
                    for (int i = 1; i < MAX_CATEGORIES_NUMBER; i++) categoryDistribution[i] = ids[i - 1];
                    break;
                default: // to avoid crash in future
                    System.out.println("CATEGORIES CORRUPTED!");
                    for (int i = 0; i < MAX_CATEGORIES_NUMBER; i++) {
                        categoryDistribution[i] = i + 1;
                    }
            }

        }
        shuffle(categoryDistribution);
    }

    private static void shuffle(int[] ar) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}
