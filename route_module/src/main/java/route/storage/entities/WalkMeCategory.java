package route.storage.entities;

import java.util.EnumSet;

public enum WalkMeCategory {
    ALCOHOL(1, "Выпить"),
    EAT(2, "Покушать"),
    AMUSEMENT(3, "Развлечься"),
    PARKS(4, "Прогуляться"),
    CULTURE(5, "Окультуриться");

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
