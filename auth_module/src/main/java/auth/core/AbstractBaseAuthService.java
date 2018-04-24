package auth.core;

abstract class AbstractBaseAuthService implements BaseAuthService {
    volatile boolean isStarted = false;

    void checkIsStarted() {
        if (!isStarted) {
            throw new IllegalStateException("Service must be started");
        }
    }
}
