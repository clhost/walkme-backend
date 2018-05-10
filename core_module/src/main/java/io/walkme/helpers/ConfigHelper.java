package io.walkme.helpers;

public class ConfigHelper {
    public static final String LOCAL_PROPERTIES = "core_local.properties";
    public static String FULL_DOMAIN;
    private static boolean isDomainSet = false;

    public static void setFullDomain(String fullDomain) {
        if (!isDomainSet) {
            FULL_DOMAIN = fullDomain;
            isDomainSet = true;
        }
    }
}
