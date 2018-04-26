package auth.core;

abstract class AbstractBaseAuthService implements BaseAuthService {
    private volatile boolean isStarted = false;

    boolean checkIsStarted() {
        return isStarted;
    }

    void setIsStartedTrue() {
        this.isStarted = true;
    }
}
