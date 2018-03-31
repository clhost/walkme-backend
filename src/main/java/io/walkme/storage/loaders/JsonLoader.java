package io.walkme.storage.loaders;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.walkme.services.CategoryService;
import io.walkme.services.GenericEntityService;
import io.walkme.services.PlaceService;
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

        Runtime.getRuntime().addShutdownHook(new Thread(Dropper::drop));

        System.out.println("Starting...");
        CategoryService.upload();
        long a = System.currentTimeMillis();

        loader.load(new File("nodejs-dataset/spb-1.json"), WalkMeCategory.BAR);
        loader.load(new File("nodejs-dataset/spb-2.json"), WalkMeCategory.EAT);
        loader.load(new File("nodejs-dataset/spb-3.json"), WalkMeCategory.FUN);
        loader.load(new File("nodejs-dataset/spb-4.json"), WalkMeCategory.PARKS);
        loader.load(new File("nodejs-dataset/spb-5.json"), WalkMeCategory.WALK);

        System.out.println(System.currentTimeMillis() - a);
    }
}
