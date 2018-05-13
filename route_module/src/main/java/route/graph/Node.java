package route.graph;


import route.storage.entities.*;
import route.utils.DateUtil;

import java.time.LocalDateTime;
import java.util.List;

public class Node {
    private String name;
    private Location point;
    private String category;
    private int categoryId;
    private String address;
    private String addressAdditional;
    private String workingTime;
    private Schedule schedule;
    private double avgCheck;
    private double rating;
    private String city;

    private Node(String name,
                 Location location,
                 String category,
                 int categoryId,
                 String address,
                 String addressAdditional,
                 String workingTime,
                 Schedule schedule,
                 double avgCheck,
                 double rank,
                 String city) {
        this.name = name;
        this.point = location;
        this.category = category;
        this.categoryId = categoryId;
        this.address = address;
        this.addressAdditional = addressAdditional;
        this.workingTime = workingTime;
        this.schedule = schedule;
        this.avgCheck = avgCheck;
        this.rating = rank;
        this.city = city;
    }

    public static Node of(Place place) {
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

        return new Node(
                place.getName(),
                place.getLocation(),
                place.getGisCategory(),
                place.getCategory().getId(),
                place.getAddressName(),
                place.getAddressComment(),
                builder.toString(),
                place.getSchedule(),
                place.getAvgCheck(),
                place.getRank(),
                place.getCity());
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

    public Schedule getSchedule() { return schedule;}

    public String getAddress() {
        return address;
    }

    public String getAddressAdditional() {
        return addressAdditional;
    }

    public String getWorkingTime() {
        return workingTime;
    }

    public String getName() {
        return name;
    }

    public double getAvgCheck() {
        return avgCheck;
    }

    public double getRating() {
        return rating;
    }

    public String getCity() {
        return city;
    }
}