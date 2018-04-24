package io.walkme.storage.entities;

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

    public Place() {
        // The explicit constructor for ORM
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

    @Override
    public String toString() {
        return "Place{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", categoryId=" + category.getId() +
                ", gisCategory='" + gisCategory + '\'' +
                ", addressName='" + addressName + '\'' +
                ", addressComment='" + addressComment + '\'' +
                ", location=" + location +
                ", schedule=" + schedule +
                '}';
    }
}
