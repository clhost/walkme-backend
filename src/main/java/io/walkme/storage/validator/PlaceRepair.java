package io.walkme.storage.validator;

import io.walkme.storage.entities.Category;
import io.walkme.storage.entities.Place;
import io.walkme.storage.entities.WalkMeCategory;

public class PlaceRepair implements Repair<Place> {
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

        return place;
    }
}
