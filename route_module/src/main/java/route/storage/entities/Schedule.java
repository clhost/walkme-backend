package route.storage.entities;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.List;

public class Schedule {
    @Expose
    private HashMap<Day, List<ScheduleTime>> scheduleInfo;

    public Schedule() {
        scheduleInfo = new HashMap<>();
    }

    public HashMap<Day, List<ScheduleTime>> getScheduleInfo() {
        return scheduleInfo;
    }

    public void put(Day day, List<ScheduleTime> scheduleTime) {
        scheduleInfo.put(day, scheduleTime);
    }

    @Override
    public String toString() {
        return scheduleInfo.toString();
    }
}
