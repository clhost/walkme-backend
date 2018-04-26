package route.graph;

import route.storage.entities.Location;

import java.util.List;

public interface Route {
    double getDistance();

    double getTime();

    List<Location> getPointList();
}
