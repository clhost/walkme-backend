package auth.core;

import org.jetbrains.annotations.Nullable;

interface BaseAuthService {
    /**
     *
     * @param token token сессии
     * @return true, если юзер авторизован, false, если нет
     */
    boolean isUserAuthorized(String token);

    /**
     *
     * @param token token сессии
     * @return данные о юзере в строковом виде (сериализованный User из базы данных)
     */
    @Nullable
    String getUserInfo(String token);

    /**
     *
     * @param code код авторизации
     * @param state vk авторизация или ok авторизация
     * @return token сессии
     */
    @Nullable
    String authorize(String code, String state);

    /**
     *
     * @param token token сессии
     */
    void logout(String token);

    /**
     * Стартуер службу
     */
    void start();
}
