package io.walkme.core;

/**
 * About stub mode:
 *
 * [param1/param2] - возможные параметры
 * server.stub.enable=[on/off]    - включает/выключает stub режим, если stub выключен - режим prod
 * server.stub.db_reload=[on/off] - включает/выключает перезалив данных в бд
 * server.stub.auth=[on/off]      - включает/выключает авторизацию
 * server.stub.graph=[on/off]     - on: prod алгоритм, off: stub алгоритм
 *
 * Если server.stub.enable=off, все остальные опции игнорируются.
 * В stub режиме отключена SSL.
 */
public class ServerMode {
    private static boolean mode = false;
    private static Boolean auth = null;
    private static Boolean graph = null;

    static void setProdMode() {
        mode = true;
    }

    static void setAuth(boolean a) {
        if (auth != null) {
            throw new AssertionError("Auth already set " + auth + ".");
        } else {
            auth = a;
        }
    }

    static void setGraph(boolean g) {
        if (graph != null) {
            throw new AssertionError("Graph already set " + graph + ".");
        } else {
            graph = g;
        }
    }

    public static boolean getAuth() {
        if (auth == null) {
            return false;
        } else {
            return auth;
        }
    }

    public static boolean getGraph() {
        if (graph == null) {
            return false;
        } else {
            return graph;
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
