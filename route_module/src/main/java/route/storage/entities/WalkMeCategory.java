package route.storage.entities;

import java.util.EnumSet;

public enum WalkMeCategory {
    ALCOHOL(1, "Выпивка"),
    EAT(2, "Еда"),
    AMUSEMENT(3, "Развлечения"),
    PARKS(4, "Прогулки"),
    CULTURE(5, "Культура");

    private final String description;
    private final int id;

    WalkMeCategory(int id, String description) {
        this.description = description;
        this.id = id;
    }

    @Override
    public String toString() {
        return "[" +
                "id=" + id + ", " +
                "desc=" + description +
                "]";
    }

    public int id() {
        return id;
    }

    public String description() {
        return description;
    }

    public static EnumSet<WalkMeCategory> getAll() {
        return EnumSet.of(ALCOHOL, EAT, AMUSEMENT, PARKS, CULTURE);
    }
}
