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
            places.add(service.get("5348552838479901_m5m5mkp8p714659853IG0GGG42zf3z26" +
                    "G6G495BH4HB11J25rgewB415159I30I0G867I5G4ewgBuvG455289396AB88H3J2H8a", PlaceFields.ID));
            places.add(service.get("5348552838557902_ksjfp5p8p714659853IGGG4eAct3y826" +
                    "G6G445B3G5B12J9HrgewB4151C2IG1I0G82J3JG3lekouvG455289397891H2A3H", PlaceFields.ID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Place randomPlace() {
        Random random = new Random();
        int p = random.nextInt(places.size());

        return places.get(p);
    }

    public static Place get0() {
        return places.get(0);
    }

    public static Place get1() {
        return places.get(1);
    }
}
