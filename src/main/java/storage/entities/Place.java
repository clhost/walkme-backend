package storage.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.*;

@Entity
@Table(name = "wm_place")
public class Place {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @Expose
    private String id;

    @Column(name = "place_name", nullable = false)
    @Expose
    private String name;

    @Column(name = "category_id", nullable = false)
    @Expose
    private int categoryId;

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

    @Column(name = "json_schedule")
    private String scheduleAsJsonString;

    public Place() {

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String getGisCategory() {
        return gisCategory;
    }

    public String getAddressName() {
        return addressName;
    }

    public String getAddressComment() {
        return addressComment;
    }

    public Location getLocation() {
        return location;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public String getScheduleAsJsonString() {
        return scheduleAsJsonString;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setGisCategory(String gisCategory) {
        this.gisCategory = gisCategory;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public void setAddressComment(String addressComment) {
        this.addressComment = addressComment;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public void setScheduleAsJsonString(String scheduleAsJsonString) {
        this.scheduleAsJsonString = scheduleAsJsonString;
    }

    @Override
    public String toString() {
        return "(" +
                id + ", " +
                name + ", " +
                categoryId + ", " +
                gisCategory + ", (" +
                addressName + "), " +
                addressComment + ", " +
                location + ", " +
                schedule +
                ")";
    }
}
