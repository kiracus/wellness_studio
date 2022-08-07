package localDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import localDatabase.lightExercise.LightExercise;
import localDatabase.lightExercise.LightExerciseDao;
import localDatabase.userInfo.User;
import localDatabase.userInfo.UserDao;
import localDatabase.wakeUpAndSleepGoal.SleepGoal;
import localDatabase.wakeUpAndSleepGoal.SleepGoalDao;
import localDatabase.utils.DateConverter;
import localDatabase.utils.TimeConverter;

@Database(entities = {
        User.class,
        LightExercise.class,
        SleepGoal.class},
        //if you changed something in the table (e.g. add a column in User.class, don't forget
        //update the version number (+1) here, or it will throw an error, also by default,
        // everytime the database schema gets changed, all previous data will be cleared and start from a new table
        version = 17,
        exportSchema = false)
@TypeConverters({DateConverter.class, TimeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();

    public abstract LightExerciseDao lightExerciseDao();

    public abstract SleepGoalDao sleepGoalDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDbInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "local DB for User")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

}
