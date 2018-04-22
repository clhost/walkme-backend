package io.walkme.response.route;

import com.google.gson.*;
import io.walkme.storage.entities.Location;

import java.util.List;

public class RouteBuilder {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();//new Gson();

    public static String asJson(int status, List<ResponseRouteEntity> entities, List<List<Location>> points) {
        JsonObject jsonObject = new JsonObject();

        Way[] ways = new Way[points.size()];
        for (int i = 0; i < ways.length; i++) {
            ways[i] = new Way(points.get(i));
        }

        String placePoints = gson.toJson(entities);
        String allPoints = gson.toJson(ways);

        JsonObject places = new JsonObject();
        places.add("places", new JsonParser().parse(placePoints));
        places.add("ways", new JsonParser().parse(allPoints));

        jsonObject.addProperty("status", status);
        jsonObject.add("result", places);

        return gson.toJson(jsonObject);
        //return jsonObject.toString();
    }

    static class Way {
        private List<Location> points;

        Way(List<Location> points) {
            this.points = points;
        }
    }
}
