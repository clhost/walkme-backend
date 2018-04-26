package route.storage.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Location {
    @Column(name = "lat", nullable = false)
    @Expose
    private double lat;

    @Column(name = "lng", nullable = false)
    @Expose
    private double lng;

    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Location() {
        // The explicit constructor for ORM
    }

    public double getLat(){
        return lat;
    }

    public double getLng(){
        return lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "[" + lat + ", " + lng + "]";
    }
}
