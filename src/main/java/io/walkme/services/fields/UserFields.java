package io.walkme.services.fields;


public enum UserFields {
    ID("id"),
    SALT("salt"),
    FIRST_NAME("f_name"),
    LAST_NAME("l_name"),
    SOCIAL_ID("social_id"),
    AVATAR("avatar"),
    TOKEN("token");

    private final String name;

    UserFields(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
