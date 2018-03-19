package entities;

import java.util.HashMap;

public class Schedule {
    private HashMap<Day, ScheduleTime> scheduleInfo;

    public Schedule(HashMap<Day, ScheduleTime> si){
        scheduleInfo = si;
    }

    public HashMap<Day, ScheduleTime> getScheduleInfo() {
        return scheduleInfo;
    }
}