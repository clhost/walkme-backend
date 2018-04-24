package io.walkme.storage.entities;


import com.google.gson.annotations.Expose;

public class ScheduleTime {
    @Expose
    private long start;

    @Expose
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
        return "[sw=" + start + ", fw=" + finish + "]";
    }
}
