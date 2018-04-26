package route.graph;

import com.graphhopper.GHResponse;
import com.graphhopper.util.PointList;
import route.storage.entities.Location;

import java.util.ArrayList;
import java.util.List;


class GraphHooperRoute implements Route {
    private GHResponse ghResponse;

    GraphHooperRoute(GHResponse ghResponse) {
        this.ghResponse = ghResponse;
    }

    @Override
    public double getDistance() {
        return ghResponse.getBest().getDistance();
    }

    @Override
    public double getTime() {
        return ghResponse.getBest().getTime();
    }

    @Override
    public List<Location> getPointList() {
        PointList pl = ghResponse.getBest().getPoints();
        ArrayList<Location> result = new ArrayList<>();
        for (int i = 0; i < pl.size(); i++) {
            result.add(new Location(pl.getLatitude(i), pl.getLongitude(i)));
        }
        return result;
    }


}
