package auth.core;

import auth.helpers.OKHelper;
import auth.helpers.VKHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import auth.entities.User;
import auth.entities.UserFields;
import auth.oauth.OAuthOkAuthorizer;
import auth.oauth.OAuthVkAuthorizer;
import auth.services.EntityService;
import auth.services.SessionService;
import auth.services.UserService;
import auth.utils.HibernateUtil;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class AuthService extends AbstractBaseAuthService {
    private final SessionService sessionService;
    private final OkHttpClient okHttpClient;
    private final EntityService<User, String, UserFields> userService;
    private final Gson gson;

    public AuthService() {
        this.sessionService = SessionService.getInstance();
        this.userService = new UserService();
        this.okHttpClient = new OkHttpClient()
                .newBuilder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();
        this.gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    }

    @Override
    public boolean isUserAuthorized(String token) {
        checkIsStarted();
        return sessionService.isSessionExist(token);
    }

    @Override
    public String getUserInfo(String token) {
        checkIsStarted();
        User user;
        try {
            user = userService.get(token, UserFields.TOKEN);
        } catch (Exception e) {
            return gson.toJson(error("user not found"));
        }
        return gson.toJson(user);
    }

    @Nullable
    @Override
    public String authorize(String code, String state) {
        checkIsStarted();
        switch (state) {
            case "vk" :
                return new OAuthVkAuthorizer(okHttpClient).authorize(code);
            case "ok":
                return new OAuthOkAuthorizer(okHttpClient).authorize(code);
            default:
                return gson.toJson(error("invalid state"));
        }
    }

    public synchronized void start() {
        if (!isStarted) {
            HibernateUtil.start();
            HibernateUtil.setNamesUTF8();
            VKHelper.init();
            OKHelper.init();
            sessionService.loadFromDatabase();
            isStarted = true;
        }
    }

    private String error(String error) {
        JsonObject object = new JsonObject();
        object.addProperty("error", error);
        return object.getAsString();
    }
}
