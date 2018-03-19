package storage.entities;


public class ScheduleTime {
    private long start;
    private long finish;

    public ScheduleTime(long start, long finish) {
        this.start = start;
        this.finish = finish;
    }

    public long getStart() {
        return start;
    }

    public long getFinish() {
        return finish;
    }

    @Override
    public String toString() {
        return "[sw=" + start +
                ", fw=" + finish +
                "]";
    }
}
