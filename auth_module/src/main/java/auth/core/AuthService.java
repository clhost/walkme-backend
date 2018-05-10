package auth.core;

import auth.helpers.FBHelper;
import auth.helpers.OKHelper;
import auth.helpers.VKHelper;
import auth.oauth.OAuthFbAuthorizer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        this.gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    @Override
    public boolean isUserAuthorized(String token) throws IllegalStateException {
        if (!checkIsStarted()) {
            throw new IllegalStateException("Service must be started");
        }
        return sessionService.isSessionExist(token);
    }

    @Nullable
    @Override
    public String getUserInfo(String token) throws IllegalStateException {
        if (!checkIsStarted()) {
            throw new IllegalStateException("Service must be started");
        }
        User user;
        try {
            user = userService.get(token, UserFields.TOKEN);
        } catch (Exception e) {
            return null;
        }
        return gson.toJson(user);
    }

    @Nullable
    @Override
    public String authorize(String code, String state) throws IllegalStateException {
        if (!checkIsStarted()) {
            throw new IllegalStateException("Service must be started");
        }
        switch (state) {
            case "vk" :
                return new OAuthVkAuthorizer(okHttpClient).authorize(code);
            case "ok":
                return new OAuthOkAuthorizer(okHttpClient).authorize(code);
            case "fb":
                return new OAuthFbAuthorizer(okHttpClient).authorize(code);
            default:
                return null;
        }
    }

    @Override
    public void logout(String token) throws IllegalStateException {
        if (!checkIsStarted()) {
            throw new IllegalStateException("Service must be started");
        }
        if (isUserAuthorized(token)) {
            sessionService.deleteSession(token);
        }
    }

    @Override
    public synchronized void start() {
        if (!checkIsStarted()) {
            HibernateUtil.start();
            HibernateUtil.setNamesUTF8();
            VKHelper.init();
            OKHelper.init();
            FBHelper.init();
            sessionService.loadFromDatabase();
            setIsStartedTrue();
        }
        System.out.println("Auth strings:");
        System.out.println("\tVK: " + VKHelper.authString());
        System.out.println("\tOK: " + OKHelper.authString());
        System.out.println("\tFB: " + FBHelper.authString());
    }
}
