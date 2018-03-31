package io.walkme.storage.validator;

import io.walkme.storage.entities.Category;
import io.walkme.storage.entities.Place;
import io.walkme.storage.entities.WalkMeCategory;

public class PlaceRepair implements Repair<Place> {
    private static final String DEFAULT_SCHEDULE =
                    "{\"fri\":{\"" +
                        "workingHours\":[" +
                            "{\"to\":\"24:00\"," +
                            "\"from\":\"00:00\"}" +
                        "]}," +
                    "\"mon\":{" +
                        "\"workingHours\":[" +
                            "{\"to\":\"24:00\"," +
                            "\"from\":\"00:00\"}" +
                    "]}," +
                    "\"sat\":{" +
                        "\"workingHours\":[" +
                            "{\"to\":\"24:00\"," +
                            "\"from\":\"00:00\"}" +
                    "]}," +
                    "\"sun\":{" +
                        "\"workingHours\":[" +
                            "{\"to\":\"24:00\"," +
                            "\"from\":\"00:00\"}" +
                    "]}," +
                    "\"thu\":{" +
                        "\"workingHours\":[" +
                            "{\"to\":\"24:00\"," +
                            "\"from\":\"00:00\"}" +
                    "]}," +
                    "\"tue\":{" +
                        "\"workingHours\":[" +
                            "{\"to\":\"24:00\"," +
                            "\"from\":\"00:00\"}" +
                    "]}," +
                    "\"wed\":{" +
                        "\"workingHours\":[" +
                            "{\"to\":\"24:00\"," +
                            "\"from\":\"00:00\"}" +
                    "]}}";

    @Override
    public Place repair(Place place, WalkMeCategory c) {
        if (place.getGisCategory() == null) {
            place.setGisCategory(c.description());
        }

        if (place.getCategory() == null) {
            place.setCategory(new Category(c.id(), c.description()));
        }

        if (place.getAddressComment() == null) {
            place.setAddressComment(" ");
        }

        if (place.getScheduleAsJsonString() == null) {
            place.setScheduleAsJsonString(DEFAULT_SCHEDULE);
        }

        return place;
    }
}
