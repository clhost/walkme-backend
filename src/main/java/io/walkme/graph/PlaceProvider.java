package io.walkme.graph;

import io.walkme.services.GenericEntityService;
import io.walkme.services.PlaceService;
import io.walkme.services.fields.PlaceFields;
import io.walkme.storage.entities.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlaceProvider {
    private static final List<Place> places = new ArrayList<>();
    private static final GenericEntityService<Place, String> service = new PlaceService();

    static {
        try {
            places.add(service.get("5348552838481127_Bhqy9lp8p714845B563GG0GGvndmi" +
                    "A26G6G44549G5322AH2rgewB415158IG2AG0J196J2Jbm2duvG45528384A7A497J3HHH7i", PlaceFields.ID));
            places.add(service.get("5348552838504355_jBrnnqp8p714845B563GG0GGu8nuk" +
                    "526G6G44547G56458H2rgewB415156IG27G0J196J2Jfp4iuvG45528384A82329J3HHH55", PlaceFields.ID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Place randomPlace() {
        Random random = new Random();
        int p = random.nextInt(places.size());

        return places.get(p);
    }
}
