package io.walkme.graph.stub;

import com.graphhopper.GHResponse;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint3D;
import io.walkme.storage.entities.Location;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphHooperRoute implements Route {
    private GHResponse ghResponse;

    GraphHooperRoute(GHResponse ghResponse){
        this.ghResponse = ghResponse;
    }

    @Override
    public double getDistance(){
        return ghResponse.getBest().getDistance();
    }

    @Override
    public long getTime() {
        return ghResponse.getBest().getTime();
    }

    @Override
    public List<Location> getPoints() {
        List<Location> locations = new ArrayList<>();
        PointList pointList = ghResponse.getBest().getPoints();

        for (GHPoint3D point : pointList) {
            locations.add(new Location(point.getLat(), point.getLon()));
        }

        return locations;
    }
}
