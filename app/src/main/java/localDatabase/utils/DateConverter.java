package localDatabase.utils;

import androidx.room.TypeConverter;

import java.sql.Date;

public class DateConverter {
    @TypeConverter
    public static Long fromDate(Date dateLong) {
        return dateLong == null ? null: dateLong.getTime();
    }

    @TypeConverter
    public static Date toDate(Long date) {
        return date == null ? null: new Date(date);
    }
}
