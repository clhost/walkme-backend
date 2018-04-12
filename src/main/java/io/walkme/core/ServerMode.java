package io.walkme.core;

public class GlobalProps {
    private static Boolean isStub = null;

    public static void setStub(boolean stub) {
        if (isStub != null) {
            throw new AssertionError("Already set " + isStub);
        } else {
            isStub = stub;
        }
    }

    public static Boolean getStub() {
        return isStub;
    }
}
