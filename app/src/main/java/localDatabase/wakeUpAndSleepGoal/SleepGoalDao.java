package localDatabase.wakeUpAndSleepGoal;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SleepGoalDao {
    @Query("SELECT * FROM SleepGoalTable")
    List<SleepGoal> getSleepGoal();

    @Insert
    void insertSleepGoal(SleepGoal ...sleepGoals);

    @Update
    void updateSleepGoal(SleepGoal leepGoal);

    @Delete
    void deleteSleepGoal(SleepGoal sleepGoal);
}
