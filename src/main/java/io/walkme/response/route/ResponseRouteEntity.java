package io.walkme.response.route;

import io.walkme.storage.entities.Day;
import io.walkme.storage.entities.Location;
import io.walkme.storage.entities.Place;
import io.walkme.storage.entities.ScheduleTime;
import io.walkme.utils.DateUtil;

import java.time.LocalDateTime;
import java.util.List;

public class RouteEntity {
    private Location point;
    private String name;
    private String category;
    private int categoryId;
    private String address;
    private String addressAdditional;
    private String workingTime;

    public RouteEntity(Location location,
                       String name,
                       String category,
                       int categoryId,
                       String address,
                       String addressAdditional,
                       String workingTime) {
        this.point = location;
        this.name = name;
        this.category = category;
        this.categoryId = categoryId;
        this.address = address;
        this.addressAdditional = addressAdditional;
        this.workingTime = workingTime;
    }

    public static RouteEntity of(Place place) {
        LocalDateTime date = LocalDateTime.now();
        int dayOfWeek = date.getDayOfWeek().getValue();

        long start;
        long finish;
        StringBuilder builder = new StringBuilder();

        List<ScheduleTime> times = place.getSchedule().getScheduleInfo().get(Day.getByDayOfWeek(dayOfWeek));
        for (int i = 0; i < times.size(); i++) {
            start = times.get(i).getStart();
            finish = times.get(i).getFinish();

            if (start == -1 || finish == -1) {
                builder.append("Сегодня не работает.");
                break;
            }

            if (i == times.size() - 1) {
                builder.append("с ")
                        .append(DateUtil.fromLongToHHMM(start))
                        .append(" до ")
                        .append(DateUtil.fromLongToHHMM(finish));
            } else {
                builder.append("с ")
                        .append(DateUtil.fromLongToHHMM(start))
                        .append(" до ")
                        .append(DateUtil.fromLongToHHMM(finish))
                        .append(", ");
            }
        }

        return new RouteEntity(
                place.getLocation(),
                place.getName(),
                place.getGisCategory(),
                place.getCategory().getId(),
                place.getAddressName(),
                place.getAddressComment(),
                builder.toString());
    }
}