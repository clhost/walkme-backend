package route.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import route.graph.Node;
import route.graph.RouteHolder;
import route.graph.ways.MSKWays;
import route.graph.ways.SPBWays;
import route.graph.exceptions.NotEnoughPointsException;
import route.graph.exceptions.NotInitializedException;
import route.graph.exceptions.StartPointIsNotAvailableException;
import route.mappers.Mapper;
import route.mappers.NodeToResponseRouteEntityMapper;
import route.services.*;
import route.services.fields.SavedRouteFields;
import route.storage.entities.Location;
import route.storage.entities.SavedRoute;
import route.storage.entities.User;
import route.utils.HibernateUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RouteService extends AbstractRouteService {
    private final EntityService<SavedRoute, String, SavedRouteFields> savedRouteService;
    private final CategoryService categoryService;
    private final Gson gson;
    private final Mapper<ResponseRouteEntity, Node> mapper = new NodeToResponseRouteEntityMapper();
    private static final Logger logger = LogManager.getLogger(RouteService.class);

    public RouteService() {
        this.categoryService = new CategoryService();
        this.savedRouteService = new SavedRouteService();
        this.gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    }

    @Nullable
    @Override
    public String getCategories() throws IllegalStateException {
        if (!checkIsStarted()) {
            throw new IllegalStateException("Service must be started");
        }
        return gson.toJson(categoryService.getCategories());
    }

    @Override
    public String getRoute(double lat, double lng, int[] categories) throws IllegalStateException {
        if (!checkIsStarted()) {
            throw new IllegalStateException("Service must be started");
        }
        try {
            RouteHolder routeHolder = null;
            if (SPBWays.isPointValid(lat, lng)) {
                routeHolder = getSpbRoute(lat, lng, categories);
            }

            if (MSKWays.isPointValid(lat, lng)) {
                routeHolder = getMskRoute(lat, lng, categories);
            }

            if (routeHolder == null) {
                return "{\"error\": \"unreachable point\"}";
            }

            List<ResponseRouteEntity> entities = new ArrayList<>();
            for (Node node : routeHolder.getPlaces()) {
                entities.add(mapper.map(node));
            }
            return RouteBuilder.asJson(entities, routeHolder.getPoints());
        } catch (NotInitializedException e) {
            throw new IllegalStateException("Service must be started");
        } catch (StartPointIsNotAvailableException e) {
            return "{\"error\": \"start point is not available\"}";
        } catch (NotEnoughPointsException e) {
            return "{\"error\": \"not enough points\"}";
        }
    }

    @Override
    public void saveRoute(String userId, String route) {
        User user = new User();
        user.setId(Long.parseLong(userId));

        SavedRoute savedRoute = new SavedRoute();
        savedRoute.setUser(user);
        savedRoute.setJsonRoute(route);

        savedRouteService.save(savedRoute);
    }

    @Nullable
    @Override
    public List<String> getSavedRoutes(String userId) {
        List<String> criteria = new ArrayList<>();
        criteria.add(userId);
        List<SavedRoute> savedRoutes = savedRouteService.getAll(criteria, SavedRouteFields.ID);
        return savedRoutes.stream().map(SavedRoute::getJsonRoute).collect(Collectors.toList());
    }

    @Override
    public void start() {
        if (!checkIsStarted()) {
            startHibernate();
            initGraph();
            setIsStartedTrue();
            logger.info("Route service has been started.");
        }
    }

    private void initGraph() {
        // load all places in RAM
        PlaceHolder.load();
        // start algorithm for SPB
        SPBWays.ghStart();
        SPBWays.initializePlaces(
                PlaceHolder.getAll().stream().filter(x -> x.getCity().equals("spb")).collect(Collectors.toList()));
        // start algorithm for MSK
        MSKWays.ghStart();
        MSKWays.initializePlaces(
                PlaceHolder.getAll().stream().filter(x -> x.getCity().equals("msk")).collect(Collectors.toList()));
    }

    private void startHibernate() {
        HibernateUtil.start();
        HibernateUtil.setNamesUTF8();
    }

    private RouteHolder getSpbRoute(double lat, double lng, int[] categories)
            throws NotInitializedException, StartPointIsNotAvailableException, NotEnoughPointsException {
        return new SPBWays(System.currentTimeMillis(), new Location(lat, lng), categories).getWays();
    }

    private RouteHolder getMskRoute(double lat, double lng, int[] categories)
            throws NotInitializedException, StartPointIsNotAvailableException, NotEnoughPointsException {
        return new MSKWays(System.currentTimeMillis(), new Location(lat, lng), categories).getWays();
    }
}
