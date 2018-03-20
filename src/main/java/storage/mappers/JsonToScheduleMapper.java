package storage.mappers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import storage.entities.Day;
import storage.entities.Schedule;
import storage.entities.ScheduleTime;
import utils.DateUtil;

import java.util.HashMap;
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
                schedule.put(days.get(e.getKey()), null);
                continue;
            }

            JsonArray arr = e.getValue().getAsJsonObject().get("workingHours").getAsJsonArray();

            String to = arr.get(0).getAsJsonObject().get("to").getAsString();
            String from = arr.get(0).getAsJsonObject().get("from").getAsString();

            ScheduleTime scheduleTime =
                    new ScheduleTime(DateUtil.fromHHMMToLong(from), DateUtil.fromHHMMToLong(to));

            schedule.put(days.get(e.getKey()), scheduleTime);
        }

        return schedule;
    }
}
