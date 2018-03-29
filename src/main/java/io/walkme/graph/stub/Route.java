package io.walkme.graph.stub;

import io.walkme.storage.entities.Location;

import java.util.List;

public interface Route {
    double getDistance();
    long getTime();
    List<Location> getPoints();
}
