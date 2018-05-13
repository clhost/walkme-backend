package route.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import route.storage.entities.Location;

import java.util.List;

class RouteBuilder {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String JSON_ROUTE = "route";
    private static final String JSON_PLACES = "places";
    private static final String JSON_WAYS = "ways";
    private static final String START_POS = "startPosition";

    static String asJson(List<ResponseRouteEntity> entities, List<List<Location>> points, Location startPos) {
        Way[] ways = new Way[points.size()];
        for (int i = 0; i < ways.length; i++) {
            ways[i] = new Way(points.get(i));
        }

        String placePoints = gson.toJson(entities);
        String allPoints = gson.toJson(ways);
        String startPosition = gson.toJson(startPos);

        JsonObject places = new JsonObject();
        places.add(START_POS, new JsonParser().parse(startPosition));
        places.add(JSON_PLACES, new JsonParser().parse(placePoints));
        places.add(JSON_WAYS, new JsonParser().parse(allPoints));

        JsonObject jsonObject = new JsonObject();
        jsonObject.add(JSON_ROUTE, places);

        return gson.toJson(jsonObject);
    }

    static class Way {
        @SuppressWarnings("unused")
        private List<Location> points;

        Way(List<Location> points) {
            this.points = points;
        }
    }
}
