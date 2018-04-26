package route.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import route.graph.Node;
import route.graph.RouteHolder;
import route.graph.Ways;
import route.graph.exceptions.NotEnoughPointsException;
import route.graph.exceptions.NotInitializedException;
import route.graph.exceptions.StartPointIsNotAvailableException;
import route.mappers.Mapper;
import route.mappers.NodeToResponseRouteEntityMapper;
import route.services.*;
import route.services.fields.SavedRouteFields;
import route.storage.entities.Location;
import route.storage.entities.SavedRoute;
import route.storage.entities.WalkMeCategory;
import route.storage.loaders.JsonLoader;
import route.storage.loaders.Loader;
import route.utils.HibernateUtil;

import javax.annotation.Nullable;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class RouteService extends AbstractRouteService {
    private final EntityService<SavedRoute, String, SavedRouteFields> savedRouteService;
    private final CategoryService categoryService;
    private final Gson gson;
    private final Mapper<ResponseRouteEntity, Node> mapper = new NodeToResponseRouteEntityMapper();

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
            Ways route = new Ways(System.currentTimeMillis(), new Location(lat, lng), categories);
            RouteHolder routeHolder = route.getWays();

            List<ResponseRouteEntity> entities = new ArrayList<>();
            for (Node node : routeHolder.getPlaces()) {
                entities.add(mapper.map(node));
            }
            return RouteBuilder.asJson(entities, routeHolder.getPoints());
        } catch (NotInitializedException e) {
            throw new IllegalStateException("Service must be started");
        } catch (StartPointIsNotAvailableException e) {
            return "{\"error:\" \"start point is not available\"}";
        } catch (NotEnoughPointsException e) {
            return "{\"error:\" \"not enough points\"}";
        }
    }

    @Nullable
    @Override
    public List<String> getSavedRoutes(String userId) {
        return null;
    }

    @Override
    public void start() {
        if (!checkIsStarted()) {
            startHibernate();
            if (!isExists()) {
                loadPlaces();
            }
            initGraph();
            setIsStartedTrue();
        }
    }

    private void loadPlaces() {
        Loader<File, WalkMeCategory> loader = new JsonLoader();
        categoryService.upload();
        loader.load(new File("nodejs-dataset/spb-1.json"), WalkMeCategory.BAR);
        loader.load(new File("nodejs-dataset/spb-2.json"), WalkMeCategory.EAT);
        loader.load(new File("nodejs-dataset/spb-3.json"), WalkMeCategory.FUN);
        loader.load(new File("nodejs-dataset/spb-4.json"), WalkMeCategory.PARKS);
        loader.load(new File("nodejs-dataset/spb-5.json"), WalkMeCategory.WALK);
    }

    private void initGraph() {
        // load all places in RAM
        PlaceHolder.load();
        // start algorithm
        Ways.ghStart();
        Ways.initializePlaces(PlaceHolder.getAll());
    }

    private void startHibernate() {
        HibernateUtil.start();
        HibernateUtil.setNamesUTF8();
    }

    private boolean isExists() {
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            session.beginTransaction();

            NativeQuery query = session.createNativeQuery("select count(*) from " + PlaceService.TABLE_NAME);
            BigInteger i = (BigInteger) query.getSingleResult();

            session.getTransaction().commit();

            return i.longValue() != 0;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
