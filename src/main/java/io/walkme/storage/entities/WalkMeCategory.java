package io.walkme.storage.entities;

public enum WalkMeCategory {
    BAR(1, "бар");

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
}
