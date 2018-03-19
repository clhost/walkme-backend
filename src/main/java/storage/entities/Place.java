package entities;

public class Place {
    private int id;
    private Location location;
    private Schedule schedule;

    public Place(int id, Location location, Schedule schedule) {
        this.id = id;
        this.location = location;
        this.schedule = schedule;
    }

    public int getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public Schedule getSchedule() {
        return schedule;
    }
}

