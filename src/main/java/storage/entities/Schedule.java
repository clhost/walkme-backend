package storage.entities;

import java.util.HashMap;

public class Schedule {
    private HashMap<Day, ScheduleTime> scheduleInfo;

    public Schedule(HashMap<Day, ScheduleTime> scheduleInfo){
        this.scheduleInfo = scheduleInfo;
    }

    public Schedule() {
        scheduleInfo = new HashMap<>();
    }

    public HashMap<Day, ScheduleTime> getScheduleInfo() {
        return scheduleInfo;
    }

    public void put(Day day, ScheduleTime scheduleTime) {
        scheduleInfo.put(day, scheduleTime);
    }

    @Override
    public String toString() {
        return scheduleInfo.toString();
    }
}