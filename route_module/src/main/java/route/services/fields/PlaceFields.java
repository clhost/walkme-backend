package route.services.fields;


public enum PlaceFields {
    ID("id"),
    NAME("place_name"),
    CATEGORY_ID("category_id"),
    GIS_CATEGORY("gis_category"),
    ADDRESS_NAME("address_name"),
    ADDRESS_COMMENT("address_comment");

    private final String name;

    PlaceFields(String name) {
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
