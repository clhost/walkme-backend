package route.services.fields;


public enum SavedRouteFields {
    ID("id"),
    USER_ID("user_id");

    private final String name;

    SavedRouteFields(String name) {
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
