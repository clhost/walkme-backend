package io.walkme.storage.loaders;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.walkme.response.route.RouteBuilder;
import io.walkme.response.route.RouteEntity;
import io.walkme.services.CategoryService;
import io.walkme.services.GenericEntityService;
import io.walkme.services.PlaceService;
import io.walkme.services.fields.PlaceFields;
import io.walkme.storage.Dropper;
import io.walkme.storage.entities.Place;
import io.walkme.mappers.JsonToPlaceMapper;
import io.walkme.mappers.Mapper;
import io.walkme.storage.entities.WalkMeCategory;
import io.walkme.storage.validator.PlaceRepair;
import io.walkme.storage.validator.Repair;
import io.walkme.utils.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonLoader implements Loader<File> {
    private static final GenericEntityService<Place, String> placeService = new PlaceService();
    private static final Mapper<Place, JsonObject> mapper = new JsonToPlaceMapper();
    private static final Repair<Place> repair = new PlaceRepair();
    private static final Logger logger = LogManager.getLogger(JsonLoader.class);

    @Override
    public void load(File file, WalkMeCategory c) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            JsonArray jsonArray = new JsonParser().parse(builder.toString()).getAsJsonArray();

            for (JsonElement element : jsonArray) {
                Place place = mapper.map(element.getAsJsonObject());

                if (place == null) {
                    continue;
                }

                place = repair.repair(place, c);
                System.out.println("Saving: " + place);
                placeService.save(place);
            }

        } catch (Exception e) {
            //logger.error(e.getCause().getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        JsonLoader loader = new JsonLoader();
        HibernateUtil.start();

        //Runtime.getRuntime().addShutdownHook(new Thread(Dropper::drop));

        System.out.println("Starting...");
        //CategoryService.upload();
        long a = System.currentTimeMillis();

        //loader.load(new File("nodejs-dataset/spb-1.json"), WalkMeCategory.BAR);
        //loader.load(new File("nodejs-dataset/spb-2.json"), WalkMeCategory.EAT);
        //loader.load(new File("nodejs-dataset/spb-3.json"), WalkMeCategory.FUN);
        //loader.load(new File("nodejs-dataset/spb-4.json"), WalkMeCategory.PARKS);
        //loader.load(new File("nodejs-dataset/spb-5.json"), WalkMeCategory.WALK);

        /*List<Place> places = new PlaceService().getAll(Arrays.asList("1", "2", "3", "4", "5"), PlaceFields.CATEGORY_ID);
        System.out.println(System.currentTimeMillis() - a);
        System.out.println("Size: " + places.size());*/

        Place p = new PlaceService().get("5348552838504355_jBrnnqp8p714845B563GG0GGu8nuk526G6G44547" +
                "G56458H2rgewB415156IG27G0J196J2Jfp4iuvG45528384A82329J3HHH55", PlaceFields.ID);

        Place p2 = new PlaceService().get("5348552838481127_Bhqy9lp8p714845B563GG0GGvndmiA26G6G44549" +
                "G5322AH2rgewB415158IG2AG0J196J2Jbm2duvG45528384A7A497J3HHH7i", PlaceFields.ID);
        List<RouteEntity> of = new ArrayList<>();
        of.add(RouteEntity.of(p));
        of.add(RouteEntity.of(p2));
        /*for (Place place : places) {
            of.add(RouteEntity.of(place));
        }*/

        System.out.println(RouteBuilder.asJson(200, of, null));
    }
}
