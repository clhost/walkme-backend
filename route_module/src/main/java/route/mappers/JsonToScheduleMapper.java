package route.mappers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import route.storage.entities.Day;
import route.storage.entities.Schedule;
import route.storage.entities.ScheduleTime;
import route.utils.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonToScheduleMapper implements Mapper<Schedule, JsonObject> {
    private static final Map<String, Day> days = new HashMap<>();
    static {
        days.put("sun", Day.SUNDAY);
        days.put("mon", Day.MONDAY);
        days.put("tue", Day.TUESDAY);
        days.put("wed", Day.WEDNESDAY);
        days.put("thu", Day.THURSDAY);
        days.put("fri", Day.FRIDAY);
        days.put("sat", Day.SATURDAY);
    }

    @Override
    public Schedule map(JsonObject jsonObject) {
        Schedule schedule = new Schedule();
        for (Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
            if (!e.getKey().matches("(^fri$)|(^mon$)|(^sat$)|(^sun$)|(^thu$)|(^tue$)|(^wed$)")) {
                continue;
            }

            if (!e.getValue().isJsonObject()) {
                List<ScheduleTime> scheduleTimes = new ArrayList<>();
                scheduleTimes.add(new ScheduleTime(-1, -1));
                schedule.put(days.get(e.getKey()), scheduleTimes);
                continue;
            }

            JsonArray arr = e.getValue().getAsJsonObject().get("workingHours").getAsJsonArray();
            List<ScheduleTime> list = new ArrayList<>();

            String to;
            String from;
            for (int i = 0; i < arr.size(); i++) {
                to = arr.get(i).getAsJsonObject().get("to").getAsString();
                from = arr.get(i).getAsJsonObject().get("from").getAsString();
                list.add(new ScheduleTime(DateUtil.fromHHMMToLong(from), DateUtil.fromHHMMToLong(to)));
            }

            schedule.put(days.get(e.getKey()), list);
        }
        return schedule;
    }
}
