package io.walkme.response;


import com.google.gson.JsonObject;

public class ResultBuilder {
    public static String asJson(int status, Object msg, ResultType type) {
        JsonResult result = new JsonResult();
        result.setStatus(status);

        if (type == ResultType.ERROR && msg instanceof String) {
            result.setError((String) msg);
        } else if (type == ResultType.RESULT && msg instanceof JsonObject) {
            result.setResult((JsonObject) msg);
        }

        return result.getResult();
    }

    public enum ResultType {
        RESULT,
        ERROR
    }
}
