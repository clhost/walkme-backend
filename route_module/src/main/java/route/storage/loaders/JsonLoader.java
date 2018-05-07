package route.storage.loaders;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import route.graph.GraphHopperRouteChecker;
import route.graph.RouteChecker;
import route.mappers.JsonToPlaceMapper;
import route.mappers.Mapper;
import route.services.EntityService;
import route.services.PlaceService;
import route.services.fields.PlaceFields;
import route.storage.entities.Place;
import route.storage.entities.WalkMeCategory;
import route.storage.validator.PlaceRepair;
import route.storage.validator.Repair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class JsonLoader implements Loader<File, WalkMeCategory> {
    private static final EntityService<Place, String, PlaceFields> placeService = new PlaceService();
    private static final Mapper<Place, JsonObject> mapper = new JsonToPlaceMapper();
    private static final Repair<Place, WalkMeCategory> repair = new PlaceRepair();
    private static final Logger logger = LogManager.getLogger(JsonLoader.class);
    private static final RouteChecker mskChecker = new GraphHopperRouteChecker("maps/RU-MOW.osm.pbf");
    private static final RouteChecker spbChecker = new GraphHopperRouteChecker("maps/RU-SPE.osm.pbf");
    private final String city;

    static {
        mskChecker.start();
        spbChecker.start();
    }

    public JsonLoader(String city) {
        this.city = city;
    }

    @Override
    public void load(File file, WalkMeCategory c) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            JsonArray jsonArray = new JsonParser().parse(builder.toString()).getAsJsonArray();
            for (JsonElement element : jsonArray) {
                Place place = mapper.map(element.getAsJsonObject());
                if (place == null) {
                    continue;
                }

                place = repair.repair(place, c);
                place.setCity(city);
                if (checkPoint(place)) {
                    placeService.save(place);
                    System.out.println("Saved place: " + place);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }
    }

    private boolean checkPoint(Place place) {
        return mskChecker.isPointValid(place.getLocation().getLat(), place.getLocation().getLng()) ||
                spbChecker.isPointValid(place.getLocation().getLat(), place.getLocation().getLng());
    }
}
