package localDatabase.utils;

import androidx.room.TypeConverter;

import java.sql.Time;

public class TimeConverter {
    @TypeConverter
    public static Long fromTime(Time timeLong) {
        return timeLong == null ? null: timeLong.getTime();
    }

    @TypeConverter
    public static Time toTime(Long time) {
        return time == null ? null: new Time(time);
    }
}
