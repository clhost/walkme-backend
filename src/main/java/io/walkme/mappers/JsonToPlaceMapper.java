package io.walkme.mappers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.walkme.storage.entities.*;

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
                    System.err.println(jsonObject.entrySet());
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        }

        return place;
    }
}
