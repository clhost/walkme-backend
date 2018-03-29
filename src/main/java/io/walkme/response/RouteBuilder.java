package response;

import com.google.gson.*;
import storage.entities.Location;

import java.util.List;

public class RouteBuilder {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();//new Gson();

    public static String asJson(int status, List<RouteEntity> entities, List<Location> points) {
        JsonObject jsonObject = new JsonObject();

        String placePoints = gson.toJson(entities);
        String allPoints = gson.toJson(points);

        JsonObject places = new JsonObject();
        places.add("places", new JsonParser().parse(placePoints));
        places.add("points", new JsonParser().parse(allPoints));

        jsonObject.addProperty("status", status);
        jsonObject.add("result", places);

        return gson.toJson(jsonObject);
        //return jsonObject.toString();
    }
}
