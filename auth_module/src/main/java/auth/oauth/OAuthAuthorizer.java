package auth.oauth;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface OAuthAuthorizer {
    /**
     *
     * @param code oauth код авторизации
     * @return token авторизации, либо null в случае ошибки
     */
    @Nullable
    String authorize(String code);
}
