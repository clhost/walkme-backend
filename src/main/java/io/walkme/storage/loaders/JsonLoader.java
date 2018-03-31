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
}
