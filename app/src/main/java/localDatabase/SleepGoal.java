package localDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.sql.Time;
import java.sql.Date;

import localDatabase.utils.DateConverter;
import localDatabase.utils.TimeConverter;

@Entity(tableName = "SleepGoalTable")
public class SleepGoal {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @TypeConverters(DateConverter.class)
    public Date date;

    @ColumnInfo(name = "sleepTime")
    @TypeConverters(TimeConverter.class)
    public Time sleepTime;

    @ColumnInfo(name = "wakeUpTime")
    @TypeConverters(TimeConverter.class)
    public Time wakeUpTime;

    @ColumnInfo(name = "hoursOfSleep")
    public double hoursOfSleep;

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    public Time getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Time sleepTime) {
        this.sleepTime = sleepTime;
    }

    public Time getWakeUpTime() {
        return wakeUpTime;
    }

    public void setWakeUpTime(Time wakeUpTime) {
        this.wakeUpTime = wakeUpTime;
    }

    public double getHoursOfSleep() {
        return hoursOfSleep;
    }

    public void setHoursOfSleep(double hoursOfSleep) {
        this.hoursOfSleep = hoursOfSleep;
    }
}
