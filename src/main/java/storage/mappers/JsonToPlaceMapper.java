package storage.mappers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import services.PlaceService;
import storage.entities.*;
import utils.DateUtil;

import java.util.Map;

/**
 * Возвращает Place без category
 */
public class JsonToPlaceMapper implements Mapper<Place, JsonObject> {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String GIS_CATEGORY = "category";
    private static final String ADDRESS_NAME = "addressName";
    private static final String ADDRESS_COMMENT = "addressComment";
    private static final String POINT = "point";
    private static final String SCHEDULE = "schedule";

    // fixme: может ли иметь 1 общий инстанс?
    private final Mapper<Schedule, JsonObject> mapper = new JsonToScheduleMapper();

    @Override
    public Place map(JsonObject jsonObject) {
        Place place = new Place();

        for (Map.Entry<String, JsonElement> element : jsonObject.entrySet()) {
            if (element.getKey().equals(ID)) {
                place.setId(element.getValue().getAsString());
            }

            if (element.getKey().equals(NAME)) {
                place.setName(element.getValue().getAsString());
            }

            if (element.getKey().equals(GIS_CATEGORY)) {
                place.setGisCategory(element.getValue().getAsString());
            }

            if (element.getKey().equals(ADDRESS_NAME)) {
                place.setAddressName(element.getValue().getAsString());
            }

            if (element.getKey().equals(ADDRESS_COMMENT)) {
                place.setAddressComment(element.getValue().getAsString());
            }

            if (element.getKey().equals(POINT)) {
                JsonObject object = element.getValue().getAsJsonObject();
                String lat = object.get("lat").getAsString();
                String lng = object.get("lng").getAsString();

                place.setLocation(new Location(Double.parseDouble(lat), Double.parseDouble(lng)));
            }

            if (element.getKey().equals(SCHEDULE)) {
                JsonObject object = element.getValue().getAsJsonObject();

                Schedule schedule = mapper.map(object);

                place.setScheduleAsJsonString(object.toString());
                place.setSchedule(schedule);
            }
        }

        return place;
    }

    public static void main(String[] args) {
        JsonToPlaceMapper mapper = new JsonToPlaceMapper();

        String json = "{\n" +
                "    \"id\":\"70000001029496328_ep9zwip8p713311301I0GGGGxt4pbu26G6G43399G5842BH1rgewB4979A8IG1I0GAG5I45J8yoiuvG45516203B7BH2H48Afc\",\n" +
                "    \"name\":\"Feromon Group\",\n" +
                "    \"category\":\"лаунж-бар\",\n" +
                "    \"point\":\n" +
                "         {\n" +
                "            \"lat\":59.952859,\n" +
                "            \"lng\":30.21247\n" +
                "         },\n" +
                "    \"addressName\":\"Кораблестроителей, 30\",\n" +
                "    \"addressComment\":\"3 этаж\",\n" +
                "    \"schedule\":\n" +
                "         {\n" +
                "            \"fri\":{\"workingHours\":[{\"to\":\"24:00\",\"from\":\"00:00\"}]},\n" +
                "            \"mon\":{\"workingHours\":[{\"to\":\"24:00\",\"from\":\"00:00\"}]},\n" +
                "            \"sat\":{\"workingHours\":[{\"to\":\"24:00\",\"from\":\"00:00\"}]},\n" +
                "            \"sun\":{\"workingHours\":[{\"to\":\"24:00\",\"from\":\"00:00\"}]},\n" +
                "            \"thu\":{\"workingHours\":[{\"to\":\"24:00\",\"from\":\"00:00\"}]},\n" +
                "            \"tue\":{\"workingHours\":[{\"to\":\"24:00\",\"from\":\"00:00\"}]},\n" +
                "            \"wed\":{\"workingHours\":[{\"to\":\"24:00\",\"from\":\"00:00\"}]},\n" +
                "            \"is24x7\":true,\n" +
                "            \"comment\":null\n" +
                "         }\n" +
                "}";

        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        Place place = mapper.map(jsonObject);
        place.setCategory(WalkMeCategory.BAR.toString());
        PlaceService placeService = new PlaceService();

        System.out.println("Try hibernating...");
        try {
            placeService.save(place);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(place);
            System.exit(0);
        }

        try {
            Place p = placeService.get(place.getId());
            System.out.println("Getting...");
            System.out.println(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
