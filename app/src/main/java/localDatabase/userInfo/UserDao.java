package localDatabase.userInfo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import localDatabase.enums.ExerciseStatus;

@Dao
public interface UserDao {
    @Query("SELECT * FROM UserInfoTable")
    List<User> getAllUser();

    @Insert
    void insertUser(User ...users);

    @Update
    void updateUser(User user);

    @Delete
    void delete(User user);

    // there is only one user so limit to the first record
    // should select by nickname or id multiple user exists
    @Query ("SELECT sleepAlarm FROM UserInfoTable LIMIT 1")
    String getSleepAlarm();

    @Query ("SELECT wakeUpAlarm FROM UserInfoTable LIMIT 1")
    String getWakeupAlarm();

    @Query ("UPDATE UserInfoTable SET sleepAlarm = :sleepAlarm")
    void setSleepAlarm(String sleepAlarm);

    @Query ("UPDATE UserInfoTable SET wakeUpAlarm = :wakeUpAlarm")
    void setWakeupAlarm(String wakeUpAlarm);

    @Query ("SELECT sleepAlarmOn FROM UserInfoTable LIMIT 1")
    Boolean sleepAlarmOn();

    @Query("UPDATE UserInfoTable SET sleepAlarmOn = :sleepAlarmOn")
    void setSleepAlarmOn(Boolean sleepAlarmOn);

    @Query ("SELECT wakeupAlarmOn FROM UserInfoTable LIMIT 1")
    Boolean wakeupAlarmOn();

    @Query("UPDATE UserInfoTable SET wakeupAlarmOn = :wakeupAlarmOn")
    void setWakeupAlarmOn(Boolean wakeupAlarmOn);

    @Query ("SELECT wakeupAlarmSensorOn FROM UserInfoTable LIMIT 1")
    Boolean wakeupAlarmSensorOn();

    @Query("UPDATE UserInfoTable SET wakeupAlarmSensorOn = :wakeupAlarmSensorOn")
    void setWakeupAlarmSensorOn(Boolean wakeupAlarmSensorOn);

    @Query ("SELECT sleepAlarmSensorOn FROM UserInfoTable LIMIT 1")
    Boolean sleepAlarmSensorOn();

    @Query("UPDATE UserInfoTable SET sleepAlarmSensorOn = :sleepAlarmSensorOn")
    void setSleepAlarmSensorOn(Boolean sleepAlarmSensorOn);

    @Query ("SELECT wakeupAlarmIsSnoozeON FROM UserInfoTable LIMIT 1")
    Boolean wakeupAlarmIsSnoozeON();

    @Query("UPDATE UserInfoTable SET wakeupAlarmIsSnoozeON = :wakeupAlarmIsSnoozeON")
    void setWakeupAlarmIsSnoozeOn(Boolean wakeupAlarmIsSnoozeON);




    @Query ("SELECT exerciseAlarm FROM UserInfoTable LIMIT 1")
    String getExerciseReminderAlarm();

    @Query("SELECT nickname FROM UserInfoTable LIMIT 1")
    String getUserNickname();

    @Query("SELECT email FROM UserInfoTable LIMIT 1")
    String getUserEmail();

    @Query("SELECT * FROM UserInfoTable LIMIT 1")
    User getUser();

    @Query("SELECT hasLoggedInOnline FROM UserInfoTable LIMIT 1")
    Boolean getOnlineStatus();

    // update to reminder by date
    @Query("UPDATE UserInfoTable SET exerciseAlarm = :exerciseAlarm")
    void setExerciseAlarm(String exerciseAlarm);

    @Query ("SELECT exerciseAlarmOn FROM UserInfoTable LIMIT 1")
    Boolean exerciseAlarmOn();

    @Query("UPDATE UserInfoTable SET exerciseAlarmOn = :exerciseAlarmOn")
    void setExerciseAlarmOn(Boolean exerciseAlarmOn);

    @Query("SELECT userId FROM userinfotable LIMIT 1")
    String getUID();

}
