package edu.neu.madcourse.wellness_studio.utils;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import android.util.Log;

import localDatabase.AppDatabase;
import localDatabase.enums.ExerciseStatus;
import localDatabase.lightExercise.LightExercise;
import localDatabase.userInfo.User;


// provide all database related operation, will modify the local database

// more like dbService as I just merged all db work here
// all methods need an appDatabase as input, so skip step 1 of example code
public class UserService {
    // test
    private final static String TAG = "user";

    // user info
    public static User getCurrentUser(AppDatabase db) {
        //this corresponds to the UserDao class's getAllUser(L12) sql query, i'm doing 'select * from table'
        //as an example, you can create your own abstract method in the dao class by following the same pattern
        // and write your own sql query in the annotation above the method.
        List<User> userList = db.userDao().getAllUser();
        if(userList.size() != 0) {
            return userList.get(0);
        }
        else
            return null;
    }

    public static boolean checkIfUserExists(AppDatabase db) {
        return getCurrentUser(db) != null;
    }

    // create a new user with a user obj
    public static void createNewUser(AppDatabase db, User user) {
        //step 2: implement the insertUser from UserDao. class
        db.userDao().insertUser(user);
    }

    // create a new user with a user nickname (the name showing on screen)
    public static void createNewUser(AppDatabase db, String nickname) {
        User user = new User();
        user.setNickname(nickname);
        createNewUser(db, user);
        Log.v(TAG, showUserInfo(db));
    }

    public static void updateUserInfo(AppDatabase db, User user) {
        db.userDao().updateUser(user);
    }

    public static String showUserInfo(AppDatabase db) {
        User user = getCurrentUser(db);
        assert user != null;
        int user_info_id = user.getUser_info_id();
        String nickname = user.nickname;
        String email = user.email;
        Boolean hasOnlineAccount = user.hasOnlineAccount;
        String password = user.password;
        Boolean hasLoggedInOnline = user.hasLoggedInOnline;
        int userId = user.userId;
        String profileImg = user.profileImg;
        String exerciseAlarm = user.exerciseAlarm;
        String sleepAlarm = user.sleepAlarm;
        String wakeUpAlarm = user.wakeUpAlarm;
        return ("user_info_id: " + user_info_id + ", " + "username: " + nickname + ", "
                + "email: " + email + ", " + "hasOnlineAccount: " + hasOnlineAccount + ", "
                + "password: " + password + ", " + "hasLoggedInOnline: " + ", "
                + hasLoggedInOnline + "userId: " + userId + ", "
                + "profileImg" + profileImg + ", " + "exerciseAlarm: "
                + exerciseAlarm + ", " + "sleepAlarm: " + sleepAlarm + ", "
                + "wakeUpAlarm: " + wakeUpAlarm + ", ");
    }

    // light exercise
    public static LightExercise getCurrentLightExercise(AppDatabase db) {
        List<LightExercise> LightExerciseList = db.lightExerciseDao().getLightExercise();
        if(LightExerciseList.size() != 0) {
            return LightExerciseList.get(0);
        }
        else
            return null;
    }

    public static boolean checkIfLightExerciseExists(AppDatabase db) {
        return getCurrentLightExercise(db) != null;
    }

    public static ExerciseStatus getCurrentExerciseStatus(AppDatabase db) {
        // get current date
//        long mili = System.currentTimeMillis();
//        Date date = new java.sql.Date(mili);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(new Date());
        Log.d("myApp", "date: " + date);

        // set date for light exercise instance
        // no exercise data yet, set as not started
        if (!checkIfLightExerciseExists(db)) {
            return ExerciseStatus.NOT_STARTED;
        } else {
            // get status by today's date
            ExerciseStatus status =
                    db.lightExerciseDao().getLightExerciseStatusByDate(date);
            return status;
        }


    }

}
