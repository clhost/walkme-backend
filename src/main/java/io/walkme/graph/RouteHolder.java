package io.walkme.graph;

import io.walkme.storage.entities.Location;

import java.util.List;

/**
 * Created by tFNiYaFF on 24.03.2018.
 */
public class RouteHolder {
    private List<List<Location>> points;
    private List<Node> places;

    public RouteHolder(List<List<Location>> points, List<Node> places) {
        this.points = points;
        this.places = places;
    }

    public List<List<Location>> getPoints() {
        return points;
    }

    public List<Node> getPlaces() {
        return places;
    }

    @Override
    public String toString() {
        return "Node{" +
                "points=" + points +
                ", places=" + places +
                '}';
    }
}