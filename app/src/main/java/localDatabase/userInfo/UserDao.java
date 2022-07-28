package localDatabase.userInfo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

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
    @Query ("SELECT sleepAlarm FROM USERINFOTABLE LIMIT 1")
    String getSleepAlarm();

    @Query ("SELECT wakeUpAlarm FROM USERINFOTABLE LIMIT 1")
    String getWakeupAlarm();
}
