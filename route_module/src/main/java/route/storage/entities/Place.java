package route.storage.entities;

import com.google.gson.annotations.Expose;
import org.hibernate.annotations.Check;

import javax.persistence.*;

@Entity
@Table(name = "wm_place")
@Check(constraints = "city in ('spb', 'msk')")
public class Place {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @Expose
    private String id;

    @Column(name = "place_name", nullable = false)
    @Expose
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @Expose
    private Category category;

    @Column(name = "gis_category", nullable = false)
    @Expose
    private String gisCategory;

    @Column(name = "address_name", nullable = false)
    @Expose
    private String addressName;

    @Column(name = "address_comment", nullable = false)
    @Expose
    private String addressComment;

    @Embedded
    @Expose
    private Location location;

    @Transient
    @Expose
    private Schedule schedule;

    @Column(name = "json_schedule", length = 1000)
    private String scheduleAsJsonString;

    @Column(name = "average_check")
    private double avgCheck;

    @Column(name = "rank")
    private double rank;

    @Column(name = "city")
    private String city;

    public Place() {
        // The explicit constructor for ORM
        rank = 0;
        avgCheck = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getGisCategory() {
        return gisCategory;
    }

    public void setGisCategory(String gisCategory) {
        this.gisCategory = gisCategory;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddressComment() {
        return addressComment;
    }

    public void setAddressComment(String addressComment) {
        this.addressComment = addressComment;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public String getScheduleAsJsonString() {
        return scheduleAsJsonString;
    }

    public void setScheduleAsJsonString(String scheduleAsJsonString) {
        this.scheduleAsJsonString = scheduleAsJsonString;
    }

    public double getAvgCheck() {
        return avgCheck;
    }

    public void setAvgCheck(double avgCheck) {
        this.avgCheck = avgCheck;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", gisCategory='" + gisCategory + '\'' +
                ", addressName='" + addressName + '\'' +
                ", addressComment='" + addressComment + '\'' +
                ", location=" + location +
                ", schedule=" + schedule +
                ", scheduleAsJsonString='" + scheduleAsJsonString + '\'' +
                ", avgCheck=" + avgCheck +
                ", rank=" + rank +
                ", city='" + city + '\'' +
                '}';
    }
}
