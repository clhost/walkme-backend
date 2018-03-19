package entities;

public class ScheduleTime {
    private double startWorkingTime;
    private double finishWorkingTime;
    private double startBreakTime;
    private double finishBreakTime;

    public ScheduleTime(double startWorkingTime, double finishWorkingTime,
                        double startBreakTime, double finishBreakTime) {
        this.startWorkingTime = startWorkingTime;
        this.finishWorkingTime = finishWorkingTime;
        this.startBreakTime = startBreakTime;
        this.finishBreakTime = finishBreakTime;
    }

    public double getStartWorkingTime() {
        return startWorkingTime;
    }

    public double getFinishBreakTime() {
        return finishBreakTime;
    }

    public double getFinishWorkingTime() {
        return finishWorkingTime;
    }

    public double getStartBreakTime() {
        return startBreakTime;
    }
}
