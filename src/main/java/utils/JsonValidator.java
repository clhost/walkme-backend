package utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

public class JsonValidator {

    /**
     * @return is json contains null, true if yes, false is no
     */
    public static boolean nullValidate(String json) {
        boolean isNull = true;
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        for (Map.Entry<String, JsonElement> element : jsonObject.entrySet()) {
            isNull &= !element.getValue().isJsonNull();
        }

        return !isNull;
    }
}
