package storage.validator;

import storage.entities.Place;
import storage.entities.WalkMeCategory;

public class PlaceRepair implements Repair<Place> {
    @Override
    public Place repair(Place place, WalkMeCategory c) {
        if (place.getGisCategory() == null) {
            place.setGisCategory(c.toString());
        }

        if (place.getCategoryId() == 0) {
            place.setCategoryId(c.id());
        }

        if (place.getAddressComment() == null) {
            place.setAddressComment(" ");
        }

        return place;
    }
}
