package entities;

public class Location {
    private double latitude;
    private double longitude;

    public Location(double latitude, double longitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }
}
