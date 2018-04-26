package io.walkme.core;

/**
 * About stub mode:
 *
 * [param1/param2] - возможные параметры
 * server.stub.enable=[on/off]    - включает/выключает stub режим, если stub выключен - режим prod
 * server.stub.db_reload=[on/off] - включает/выключает перезалив данных в бд
 * server.stub.auth=[on/off]      - включает/выключает авторизацию
 *
 * Если server.stub.enable=off, все остальные опции игнорируются.
 * В stub режиме отключена SSL.
 */
public class ServerMode {
    private static boolean mode;
    private static Boolean auth = null;

    static void setProdMode() {
        mode = true;
        setAuth(true);
    }

    static void setStubMode() {
        mode = false;
        setAuth(true);
    }

    static void setAuth(boolean a) {
        if (auth != null) {
            throw new AssertionError("Auth already set " + auth + ".");
        } else {
            auth = a;
        }
    }

    public static boolean getAuth() {
        if (auth == null) {
            return false;
        } else {
            return auth;
        }
    }

    /**
     *
     * @return true, if prod, false, if stub
     */
    static Boolean getMode() {
        return mode;
    }
}
