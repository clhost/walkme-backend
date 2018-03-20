package storage.validator;

import storage.entities.Place;
import storage.mappers.WalkMeCategory;

public class PlaceRepair implements Repair<Place> {
    @Override
    public Place repair(Place place, WalkMeCategory c) {
        if (place.getGisCategory() == null) {
            place.setGisCategory(c.toString());
        }

        if (place.getCategory() == null) {
            place.setCategory(c.toString());
        }

        if (place.getAddressComment() == null) {
            place.setAddressComment(" ");
        }

        return place;
    }
}
