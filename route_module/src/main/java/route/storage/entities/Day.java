package route.storage.entities;


public enum Day {
    SUNDAY("sun"),
    MONDAY("mon"),
    TUESDAY("tue"),
    WEDNESDAY("wed"),
    THURSDAY("thu"),
    FRIDAY("fri"),
    SATURDAY("sat");

    private final String day;

    Day(String day) {
        this.day = day;
    }

    public static Day getByDayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return MONDAY;
            case 2:
                return TUESDAY;
            case 3:
                return WEDNESDAY;
            case 4:
                return THURSDAY;
            case 5:
                return FRIDAY;
            case 6:
                return SATURDAY;
            case 7:
                return SUNDAY;
            default:
                throw new UnsupportedOperationException("The " + dayOfWeek + "isn't supported.");
        }
    }

    @Override
    public String toString() {
        return day;
    }
}
