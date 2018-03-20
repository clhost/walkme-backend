package storage.mappers;

public enum WalkMeCategory {
    BAR("бар");

    private String category;
    WalkMeCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return category;
    }
}
