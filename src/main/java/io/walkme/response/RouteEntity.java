package io.walkme.response;

import io.walkme.storage.entities.Day;
import io.walkme.storage.entities.Location;
import io.walkme.storage.entities.Place;
import io.walkme.utils.DateUtil;

import java.time.LocalDateTime;

public class RouteEntity {
    private Location point;
    private String category;
    private int categoryId;
    private String address;
    private String addressAdditional;
    private String workingTime;

    private RouteEntity(Location location,
                String category,
                int categoryId,
                String address,
                String addressAdditional,
                String workingTime) {
        this.point = location;
        this.category = category;
        this.categoryId = categoryId;
        this.address = address;
        this.addressAdditional = addressAdditional;
        this.workingTime = workingTime;
    }

    public static RouteEntity of(Place place) {
        LocalDateTime date = LocalDateTime.now();
        int dayOfWeek = date.getDayOfWeek().getValue();

        long start = place.getSchedule().getScheduleInfo().get(Day.getByDayOfWeek(dayOfWeek)).getStart();
        long finish = place.getSchedule().getScheduleInfo().get(Day.getByDayOfWeek(dayOfWeek)).getFinish();

        return new RouteEntity(
                place.getLocation(),
                place.getGisCategory(),
                place.getCategory().getId(),
                place.getAddressName(),
                place.getAddressComment(),
                "с " + DateUtil.fromLongToHHMM(start) + " по " + DateUtil.fromLongToHHMM(finish)
                );
    }

    public Location getPoint() {
        return point;
    }

    public String getCategory() {
        return category;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getAddress() {
        return address;
    }

    public String getAddressAdditional() {
        return addressAdditional;
    }

    public String getWorkingTime() {
        return workingTime;
    }
}