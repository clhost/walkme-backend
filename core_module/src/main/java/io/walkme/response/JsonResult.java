package io.walkme.response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class JsonResult implements ServerJsonResponse<String, JsonObject, String> {
    private int status;
    private JsonObject result;
    private String error;

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void setResult(JsonObject result) throws IllegalStateException {
        if (error != null) {
            throw new IllegalStateException("Error must be empty.");
        }

        this.result = result;
    }

    @Override
    public void setError(String error) throws IllegalStateException {
        if (result != null) {
            throw new IllegalStateException("Result must be empty.");
        }

        this.error = error;
    }

    @Override
    public String getResult() throws IllegalStateException {
        if (error == null && result == null) {
            throw new IllegalStateException("Error and result can't be empty together.");
        }

        JsonObject object = new JsonObject();
        object.addProperty("status", status);

        if (error == null) {
            object.add("result", result);
        }

        if (result == null) {
            object.addProperty("error", error);
        }

        return gson.toJson(object);
    }
}
