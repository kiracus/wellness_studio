package localDatabase;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
// if you changed something in the table here (e.g. add a column in User.class,
// don't forget to go to AppDatabase.class and update the version number (+1) there
// or it will throw an error
public class User {
    @PrimaryKey
    @NonNull
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
    public int userId;

    @ColumnInfo(name = "profileImg")
    public String profileImg;

    @ColumnInfo(name = "exerciseAlarm")
    public String exerciseAlarm;

    @ColumnInfo(name = "sleepAlarm")
    public String sleepAlarm;

    @ColumnInfo(name = "wakeUpAlarm")
    public String wakeUpAlarm;

}
