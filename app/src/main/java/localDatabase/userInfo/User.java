package localDatabase.userInfo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "UserInfoTable")
// if you changed something in the table here (e.g. add a column in User.class,
// don't forget to go to AppDatabase.class and update the version number (+1) there
// or it will throw an error
public class User {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int user_info_id;

    @ColumnInfo(name = "nickname")
    public String nickname;

    @ColumnInfo(name = "has_online_account")
    public Boolean hasOnlineAccount;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "hasLoggedInOnline")
    public Boolean hasLoggedInOnline;

    @ColumnInfo(name = "userId")
    public String userId;

    @ColumnInfo(name = "profileImg")
    public String profileImg;

    @ColumnInfo(name = "exerciseAlarm")
    public String exerciseAlarm;

    @ColumnInfo(name = "sleepAlarm")
    public String sleepAlarm;

    @ColumnInfo(name = "wakeUpAlarm")
    public String wakeUpAlarm;

    @ColumnInfo(name = "exerciseAlarmOn",defaultValue = "false")
    public Boolean exerciseAlarmOn;

    @ColumnInfo(name = "sleepAlarmOn",defaultValue = "false")
    public Boolean sleepAlarmOn;

    @ColumnInfo(name = "wakeupAlarmOn",defaultValue = "false")
    public Boolean wakeupAlarmOn;

    @ColumnInfo(name = "sleepAlarmSensorOn",defaultValue = "false")
    public Boolean sleepAlarmSensorOn;

    @ColumnInfo(name = "wakeupAlarmSensorOn",defaultValue = "false")
    public Boolean wakeupAlarmSensorOn;

    @ColumnInfo(name = "wakeupAlarmIsSnoozeON",defaultValue = "false")
    public Boolean wakeupAlarmIsSnoozeON;


    public int getUser_info_id() {
        return user_info_id;
    }

    public void setUser_info_id(int user_info_id) {
        this.user_info_id = user_info_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getHasOnlineAccount() {
        return hasOnlineAccount;
    }

    public void setHasOnlineAccount(Boolean hasOnlineAccount) {
        this.hasOnlineAccount = hasOnlineAccount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getHasLoggedInOnline() {
        return hasLoggedInOnline;
    }

    public void setHasLoggedInOnline(Boolean hasLoggedInOnline) {
        this.hasLoggedInOnline = hasLoggedInOnline;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getExerciseAlarm() {
        return exerciseAlarm;
    }

    public void setExerciseAlarm(String exerciseAlarm) {
        this.exerciseAlarm = exerciseAlarm;
    }

    public String getSleepAlarm() {
        return sleepAlarm;
    }

    public void setSleepAlarm(String sleepAlarm) {
        this.sleepAlarm = sleepAlarm;
    }

    public String getWakeUpAlarm() {
        return wakeUpAlarm;
    }

    public void setWakeUpAlarm(String wakeUpAlarm) {
        this.wakeUpAlarm = wakeUpAlarm;
    }

    public Boolean getExerciseAlarmOn() { return exerciseAlarmOn; }

    public void setExerciseAlarmOn(Boolean exerciseAlarmOn) { this.exerciseAlarmOn = exerciseAlarmOn; }

    public Boolean getSleepAlarmOn() {
        return this.sleepAlarmOn;
    }

    public void setSleepAlarmOn(Boolean sleepAlarmOn) {
        this.sleepAlarmOn = sleepAlarmOn;
    }

    public Boolean getWakeupAlarmOn() {
        return this.wakeupAlarmOn;
    }

    public void setWakeupAlarmOn(Boolean wakeupAlarmOn) {
        this.wakeupAlarmOn = wakeupAlarmOn;
    }

    public Boolean getSleepAlarmSensorOn() {
        return this.sleepAlarmSensorOn;
    }

    public void setSleepAlarmSensorOn(Boolean sleepAlarmSensorOn) {
        this.sleepAlarmSensorOn = sleepAlarmSensorOn;
    }

    public Boolean getWakeupAlarmSensorOn() {
        return wakeupAlarmSensorOn;
    }

    public void setWakeupAlarmSensorOn(Boolean wakeupAlarmSensorOn) {
        this.wakeupAlarmSensorOn = wakeupAlarmSensorOn;
    }

    public Boolean getWakeupAlarmIsSnoozeON() {
        return wakeupAlarmIsSnoozeON;
    }

    public void setWakeupAlarmIsSnoozeON(Boolean wakeupAlarmIsSnoozeON) {
        this.wakeupAlarmIsSnoozeON = wakeupAlarmIsSnoozeON;
    }

}
