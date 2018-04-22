package io.walkme.response.user;

import com.google.gson.annotations.Expose;
import io.walkme.storage.entities.User;

/**
 * ResponseRouteEntity представляет собой обёртку над ответом клиенту - "урезанная" версия класса {@link User}.
 * Сериализуется в json.
 */
public class ResponseUserEntity {
    @Expose
    @SuppressWarnings("unused")
    private String name;

    @Expose
    @SuppressWarnings("unused")
    private String avatar;

    public ResponseUserEntity(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
    }
}
