package route.core;

abstract class AbstractRouteService implements BaseRouteService {
    private volatile boolean isStarted = false;

    boolean checkIsStarted() {
        return isStarted;
    }

    void setIsStartedTrue() {
        this.isStarted = true;
    }
}
