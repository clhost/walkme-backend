package route.mappers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import route.storage.entities.Location;
import route.storage.entities.Place;
import route.storage.entities.Schedule;

import java.util.Map;
import java.util.Optional;

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
    private static final String REVIEWS = "reviews";
    private static final String REVIEW_COUNT = "review_count";
    private static final String RATING = "rating";
    private static final String AVERAGE_CHECK = "averageCheck";

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
                if (element.getValue().isJsonObject()) {
                    JsonObject object = element.getValue().getAsJsonObject();
                    String lat = object.get("lat").getAsString();
                    String lng = object.get("lng").getAsString();

                    place.setLocation(new Location(Double.parseDouble(lat), Double.parseDouble(lng)));
                } else {
                    return null;
                }
            }

            if (element.getKey().equals(SCHEDULE)) {
                try {
                    if (element.getValue().isJsonObject()) {
                        JsonObject object = element.getValue().getAsJsonObject();

                        Schedule schedule = mapper.map(object);

                        place.setScheduleAsJsonString(object.toString());
                        place.setSchedule(schedule);
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }

            if (element.getKey().equals(REVIEWS)) {
                if (element.getValue().isJsonObject()) {
                    JsonObject internal = element.getValue().getAsJsonObject();
                    if (internal.get(REVIEW_COUNT) != null && internal.get(RATING) != null) {
                        place.setRank(internal.get(RATING).getAsDouble());
                    } // else rank is 0
                }
            }

            if (element.getKey().equals(AVERAGE_CHECK)) {
                if (!element.getValue().isJsonNull()) {
                    place.setAvgCheck(element.getValue().getAsDouble());
                }
            } // else average check is 0
        }

        return place;
    }
}
