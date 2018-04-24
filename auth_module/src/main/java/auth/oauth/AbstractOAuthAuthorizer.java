package auth.oauth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import auth.entities.User;
import auth.entities.UserFields;
import auth.services.EntityService;
import auth.services.SessionService;
import auth.services.UserService;
import auth.utils.MD5Encoder;
import auth.utils.SHA256HEXEncoder;
import okhttp3.*;

import java.io.IOException;
import java.util.Optional;

abstract class AbstractOAuthAuthorizer implements OAuthAuthorizer {
    final SHA256HEXEncoder tokenEncoder;
    final MD5Encoder md5Encoder;
    final SessionService sessionService;
    final OkHttpClient okHttpClient;
    final EntityService<User, String, UserFields> userService;
    final JsonParser jsonParser;

    AbstractOAuthAuthorizer(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        this.tokenEncoder = new SHA256HEXEncoder();
        this.md5Encoder = new MD5Encoder();
        this.sessionService = SessionService.getInstance();
        this.userService = new UserService();
        this.jsonParser = new JsonParser();
    }

    JsonObject error(int code, String error) {
        JsonObject object = new JsonObject();
        object.addProperty("status", code);
        object.addProperty("error", error);
        return object;
    }

    JsonObject oAuthRequest(String url, RequestType requestType) {
        Response response = null;
        Request request = null;
        try {
            switch (requestType) {
                case GET:
                    request = new Request.Builder()
                            .get()
                            .url(url)
                            .build();
                    break;
                case POST:
                    request = new Request.Builder()
                            .post(RequestBody.create(null, ""))
                            .url(url)
                            .build();
                    break;
            }
            response = okHttpClient.newCall(request).execute();

            Optional<ResponseBody> body = Optional.ofNullable(response.body());
            if (!body.isPresent()) {
                return error(502, "Bad Gateway");
            }

            String json = body.get().string();
            return jsonParser.parse(json).getAsJsonObject();
        } catch (IOException e) {
            return error(502, "Bad Gateway");
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    enum RequestType {
        GET,
        POST;
    }
}
