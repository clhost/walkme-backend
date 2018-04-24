package core;


import java.util.List;

public interface Route {
    double getDistance();

    double getTime();

    List<Location> getPointList();
}
