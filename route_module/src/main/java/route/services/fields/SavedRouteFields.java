package route.services.fields;


public enum SavedRouteFields {
    ROUTE_ID("route_id"),
    ID("id"),
    JSON_ROUTE("json_route");

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
