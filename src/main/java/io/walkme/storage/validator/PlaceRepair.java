package storage.validator;

import storage.entities.Category;
import storage.entities.Place;
import storage.entities.WalkMeCategory;

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
