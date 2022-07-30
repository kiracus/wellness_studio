package edu.neu.madcourse.wellness_studio;

public class Alarm {
    String hour, min;

    public Alarm(String hour, String min) {
        this.hour = hour;
        this.min = min;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }
}
