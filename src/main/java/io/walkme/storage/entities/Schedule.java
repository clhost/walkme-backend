package storage.entities;

import com.google.gson.annotations.Expose;

import java.util.HashMap;

public class Schedule {
    @Expose
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
