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
            places.add(service.get("5348552838479901_nehhg2p8p713845B56IG0GGGlszk" +
                    "9z26G6G433A4G5BA04HArgewB4979B3IG22G0J5CI5G4k4gyuvG45516384A7991H3J2H42", PlaceFields.ID));
            places.add(service.get("5348552838482231_wfsos6p8p713845B56IG0GGGm76B" +
                    "1r26G6G433A3G54334H7rgewB4979B2IG16G0J5CI5G4mfscuvG45516384A7A88H3J2Hgb", PlaceFields.ID));
            places.add(service.get("5348552838495522_jqjrnxp8p7136598532GGGG68cxf" +
                    "tc26G6G4339AG5732J63rgewB4979A9IG2I0G8G5I4GJyitkuvG455169396C7H1H48Ad", PlaceFields.ID));
            places.add(service.get("5348552838557427_mqr48xp8p713845B563GG0GGd57mD" +
                    "826G6G433A5G5952AH2rgewB4979B4IG24G0J18BJ2JlyfAuvG45516384A87193J3HHH23", PlaceFields.ID));
            places.add(service.get("5348552838746636_7luvwDp8p713845B563GG0GGA3yp6" +
                    "826G6G433A5G58739H2rgewB4979B4IG26G0J18BJ2J7p2yuvG45516384AA6279J3HHH8e", PlaceFields.ID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Place randomPlace() {
        Random random = new Random();
        int p = random.nextInt(4);

        return places.get(p);
    }
}
