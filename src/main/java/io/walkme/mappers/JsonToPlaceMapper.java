package mappers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.walkme.storage.entities.*;
import storage.entities.Location;
import storage.entities.Place;
import storage.entities.Schedule;

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
}
