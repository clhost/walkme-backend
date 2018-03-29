package storage.loaders;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import services.GenericEntityService;
import services.PlaceService;
import storage.entities.Place;
import mappers.JsonToPlaceMapper;
import mappers.Mapper;
import storage.entities.WalkMeCategory;
import storage.validator.PlaceRepair;
import storage.validator.Repair;
import utils.HibernateUtil;

import java.io.*;

public class JsonLoader implements Loader<File> {
    private static final GenericEntityService<Place, String> placeService = new PlaceService();
    private static final Mapper<Place, JsonObject> mapper = new JsonToPlaceMapper();
    private static final Repair<Place> repair = new PlaceRepair();

    @Override
    public void load(File file) {
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
                place = repair.repair(place, WalkMeCategory.BAR);
                System.out.println(place);
                placeService.save(place);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JsonLoader loader = new JsonLoader();
        HibernateUtil.start();

        System.out.println("Starting...");
        long a = System.currentTimeMillis();
        loader.load(new File("nodejs-dataset/bary_spb.json"));
        System.out.println(System.currentTimeMillis() - a);
    }
}
