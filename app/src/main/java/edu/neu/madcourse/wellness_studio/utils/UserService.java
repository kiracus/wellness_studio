package edu.neu.madcourse.wellness_studio.utils;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.lights.Light;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import localDatabase.AppDatabase;
import localDatabase.enums.ExerciseSet;
import localDatabase.enums.ExerciseStatus;
import localDatabase.lightExercise.LightExercise;
import localDatabase.userInfo.User;
import localDatabase.utils.DateConverter;
import localDatabase.wakeUpAndSleepGoal.SleepGoal;


// provide all database related operation, will modify the local database

// more like dbService as I just merged all db work here
// all methods need an appDatabase as input, so skip step 1 of example code
public class UserService {
    // test
    private final static String TAG = "user";

    // ================================================
    // =============      user info       =============

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
        // TODO set other properties
        user.setHasOnlineAccount(false);
        user.setHasLoggedInOnline(false);
        user.setSleepAlarmOn(false);
        user.setWakeupAlarmOn(false);
        user.setSleepAlarmSensorOn(false);
        user.setWakeupAlarmSensorOn(false);
        user.setWakeupAlarmIsSnoozeON(false);

        // user.setProfileImg("some/address");

        createNewUser(db, user);
        //Log.v(TAG, showUserInfo(db));
    }

    public static void updateUserInfo(AppDatabase db, User user) {
        db.userDao().updateUser(user);
    }

    public static String getNickname(AppDatabase db) {
        return db.userDao().getUserNickname();
    }

    public static void updateUserImg(AppDatabase db, String imgUri) {
        User user = getCurrentUser(db);
        assert user != null;
        user.setProfileImg(imgUri);
        Log.v(TAG, "update user img uri: " + imgUri);
        updateUserInfo(db, user);
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
        String userId = user.userId;
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


    // ================================================
    // =============   light exercise   ===============

    // get le obj for the current date (today)
    public static LightExercise getCurrentLightExercise(AppDatabase db) {
        return getLightExerciseByDate(db, Utils.getCurrentDate());
    }

    // get a le object for a specific date, if obj does not exist, create a new one then return that
    public static LightExercise getLightExerciseByDate(AppDatabase db, String dateInput) {
        LightExercise le =  db.lightExerciseDao().getLightExerciseByDate(dateInput);
        if (le == null) {
            //Log.v(TAG, "no le log for today, creating one");
            le = createNewLightExercise(db, dateInput);
        }
        //Log.v(TAG, "returning le obj of date: " + le.date);
        return le;
    }

    // check if any row in LightExerciseTable
    public static boolean checkIfLightExerciseExists(AppDatabase db) {
        List<LightExercise> LightExerciseList = db.lightExerciseDao().getLightExercise();
        return LightExerciseList.size() != 0;
    }

    // check if record for a specific date exists in LightExerciseTable
    public static boolean checkIfLightExerciseExists(AppDatabase db, String date) {
        return db.lightExerciseDao().getLightExerciseByDate(date) != null;
    }

    // create a new le obj for the current date and a status of "not started"
    public static LightExercise createNewLightExercise(AppDatabase db) {
        return createNewLightExercise(db, Utils.getCurrentDate());
    }

    // create a new le obj for the input date and a status of "not started"
    public static LightExercise createNewLightExercise(AppDatabase db, String date) {
        LightExercise le;
        if (!checkIfLightExerciseExists(db, date)) {
            le = new LightExercise();
            le.setDate(date);
            le.setExerciseStatus(ExerciseStatus.NOT_STARTED);
            le.setCurrentSet(ExerciseSet.NOT_SELECTED);
            le.setStepOneCompleted(false);
            le.setStepTwoCompleted(false);
            le.setStepThreeCompleted(false);
            le.setStepFourCompleted(false);
            db.lightExerciseDao().insertLightExercise(le);
            //Log.v(TAG, "new le obj created for date: " + date);
        } else {
            le = getLightExerciseByDate(db, date);
            //Log.v(TAG, "CANNOT CREATE -> le obj already exists on: " + date);
        }

        return le;
    }

    // get exercise status by date, return value will never be null
    public static ExerciseStatus getExerciseStatusByDate(AppDatabase db, String dateInput) {
        LightExercise le = getLightExerciseByDate(db, dateInput);
        return le.exerciseStatus;
    }

    // get current exercise set by date, should never return null
    public static ExerciseSet getCurrentSetByDate(AppDatabase db, String date) {
        LightExercise le = getLightExerciseByDate(db, date);
        return le.currentSet;
    }

    public static void updateExerciseStatus(AppDatabase db, ExerciseStatus status, String date) {
        if (checkIfLightExerciseExists(db)) {
            Log.v(TAG, "update status: " + status.toString());
            db.lightExerciseDao().setLightExerciseStatusByDate(status, date);
        }
    }

    // update isFinished by date
    public static void updateExerciseGoalStatus(AppDatabase db, Boolean isFinished, String date) {
        if (checkIfLightExerciseExists(db)) {
            Log.v(TAG, "updating status: " + isFinished.toString());
            getLightExerciseByDate(db, date);  // will create if not exist
            db.lightExerciseDao().setExerciseGoalByDate(isFinished, date);
        }
    }

    // update current set (for today)
    public static void updateCurrSet(AppDatabase db, ExerciseSet set) {
        if (checkIfLightExerciseExists(db)) {
            Log.v(TAG, "updating currSet: " + set.toString());
            db.lightExerciseDao().setCurrSet(set, Utils.getCurrentDate());
        }
    }

    // get a list of dates(yyyy-mm-dd) when le goal is finished in given month
    public static List<String> getFinishedDatesOfMonth(AppDatabase db, String yearMonth) {
        if (checkIfLightExerciseExists(db)) {
            //Log.v(TAG, "looking for dates finished in month: " + yearMonth);
            return db.lightExerciseDao().getFinishedDatesOfMonth(yearMonth+"%");
        } else {
            //Log.v(TAG, "no available le data in month: " + yearMonth);
            return null;
        }
    }

    // check if goal is finished at the given date
    public static Boolean getGoalFinishedByDate(AppDatabase db, String date) {
        Boolean res = db.lightExerciseDao().getGoalFinishedByDate(date);
        if (res == null) {
            // no record for that day so create one
            createNewLightExercise(db, date);
            return false;
        } else return res;
    }

    public static String getExerciseReminderAlarm(AppDatabase db) {
        String res = db.userDao().getExerciseReminderAlarm();
        if (res == null) {
            return "--:--";
        } else
            return res;
    }

    // update exercise reminder in User table
    public static void updateExerciseReminder(AppDatabase db, String exerciseAlarm) {
        if (checkIfUserExists(db)) {
            Log.v(TAG, "updating exercise reminder: " + exerciseAlarm);
            db.userDao().setExerciseAlarm(exerciseAlarm);
        }
    }

    public static Boolean getExerciseAlarmOn(AppDatabase db) {
        return db.userDao().exerciseAlarmOn();
    }

    public static void updateStepCompletion(AppDatabase db, int currentStep, boolean stepCompleted) {
        if(currentStep == 1) {
            db.lightExerciseDao().setStepOneCompleted(stepCompleted,Utils.getCurrentDate());
        }
        if(currentStep == 2) {
            db.lightExerciseDao().setStepTwoCompleted(stepCompleted,Utils.getCurrentDate());
        }
        if(currentStep == 3) {
            db.lightExerciseDao().setStepThreeCompleted(stepCompleted,Utils.getCurrentDate());
        }
        if(currentStep == 4) {
            db.lightExerciseDao().setStepFourCompleted(stepCompleted,Utils.getCurrentDate());
        }
        Log.v(TAG, "updating exerciseStepCompletion: " + currentStep);
    }

    // update current set (for today)
    public static void updateCurrentStep(AppDatabase db, int currentStep) {
        if (checkIfLightExerciseExists(db)) {
            Log.v(TAG, "updating currSet: " + currentStep);
            db.lightExerciseDao().setLightExerciseStepByDate(String.valueOf(currentStep), Utils.getCurrentDate());
        }
    }

    // update exercise reminder on or off in User table
    public static void updateExerciseReminderOn(AppDatabase db, Boolean exerciseAlarmOn) {
        if (checkIfUserExists(db)) {
            Log.v(TAG, "updating exercise reminder: " + exerciseAlarmOn);
            db.userDao().setExerciseAlarmOn(exerciseAlarmOn);
        }
    }

    public static int getWeeklyFinishedCount(AppDatabase db) {
        Calendar mCalendar = Calendar.getInstance();
        int counts = 0;
        for (int i = 1; i < 8; i++) {  // count from day 1 of this week to day 7
            mCalendar.set(Calendar.DAY_OF_WEEK, i);
            int month = mCalendar.get(Calendar.MONTH) + 1;
            int day = mCalendar.get(Calendar.DAY_OF_MONTH);
            int year = mCalendar.get(Calendar.YEAR);
            String dayKey = year + "-" + to2CharString(month) + "-" + to2CharString(day);
            if (UserService.getGoalFinishedByDate(db, dayKey)) {  // should not be null
                counts++;
            }
            Log.v(TAG, "checking goal status on " + dayKey + ", current counts: " + counts);
        }
        return counts;
    }
    // transfer an integer to a 2-char string, add 0 before single digit number
    private static String to2CharString(int num) {
        return num<10 ? "0"+num : ""+num;
    }

    // ================================================
    // =============   sleep goal   ===============

    public static void updateSleepAlarmOn(AppDatabase db, boolean value) {
        if (checkIfUserExists(db)) {
            db.userDao().setSleepAlarmOn(value);
            Log.v(TAG, "updating sleep alarm on: " + value);
        }
    }

    public static void updateWakeupAlarmOn(AppDatabase db, boolean value) {
        if (checkIfUserExists(db)) {
            db.userDao().setWakeupAlarmOn(value);
            Log.v(TAG, "updating wakeup alarm on: " + value);
        }
    }

    public static Boolean getSleepAlarmON(AppDatabase db) {
        Boolean res =  db.userDao().sleepAlarmOn();
        if (res == null) {
            return false;
        } else return res;
    }

    public static Boolean getWakeupAlarmON(AppDatabase db) {
        Boolean res = db.userDao().wakeupAlarmOn();
        if (res == null) {
            return false;
        } else return res;
    }


    public static Boolean getWakeupAlarmSensorOn(AppDatabase db) {
        return db.userDao().wakeupAlarmSensorOn();
    }

    public static Boolean getSleepAlarmSensorOn(AppDatabase db) {
        return db.userDao().sleepAlarmSensorOn();
    }

    public static Boolean getWakeupAlarmIsSnoozeOn(AppDatabase db) {
        return db.userDao().wakeupAlarmIsSnoozeON();
    }

    public static void updateSleepSensorOn(AppDatabase db, boolean value) {
        if (checkIfUserExists(db)) {
            db.userDao().setSleepAlarmSensorOn(value);
            Log.v(TAG, "updating sleep alarm sensor on: " + value);
        }
    }

    public static void updateWakeupSensorOn(AppDatabase db, boolean value) {
        if (checkIfUserExists(db)) {
            db.userDao().setWakeupAlarmSensorOn(value);
            Log.v(TAG, "updating wakeup alarm sensor on: " + value);
        }
    }

    public static void updateWakeupIsSnoozeOn(AppDatabase db, boolean value) {
        if (checkIfUserExists(db)) {
            db.userDao().setWakeupAlarmIsSnoozeOn(value);
            Log.v(TAG, "updating wakeup alarm snooze on: " + value);
        }
    }


    public static void updateSleepAlarm (AppDatabase db, String dateTime) {
        if (checkIfUserExists(db)) {
            db.userDao().setSleepAlarm(dateTime);
            Log.v(TAG, "updating sleep alarm: " + dateTime);
        }
    }

    public static void updateWakeupAlarm (AppDatabase db, String dateTime) {
        if (checkIfUserExists(db)) {
            db.userDao().setWakeupAlarm(dateTime);
            Log.v(TAG, "updating wakeup alarm: " + dateTime);
        }
    }

    // set sleep and wakeup alarm
    public static String getSleepAlarm(AppDatabase db) {
        String res = db.userDao().getSleepAlarm();
        if (res == null) {
            return "--:--";
        } else
            return res;
    }

    public static String getWakeupAlarm(AppDatabase db) {
        String res = db.userDao().getWakeupAlarm();
        if (res == null) {
            return "--:--";
        } else
            return res;
    }



    // ================================================
    // =============   leaderboard & login / logout  ===============

    // check if current user is online from local db
    public static boolean getOnlineStatus(AppDatabase db) {
        return db.userDao().getOnlineStatus();
    }

    // update user online status
    public static void changeOnlineStatus(AppDatabase db) {
        User user = db.userDao().getUser();
        if (user.getHasLoggedInOnline()) {
            setUserOffline(db);
        } else {
            setUserOnline(db);
        }
    }

    public static void setUserOnline(AppDatabase db) {
        User user = db.userDao().getUser();
        user.setHasLoggedInOnline(true);
        updateUserInfo(db, user);
        Log.v(TAG, "[DB] user is marked logged in.");
    }

    public static void setUserOffline(AppDatabase db) {
        User user = db.userDao().getUser();
        user.setHasLoggedInOnline(false);
        updateUserInfo(db, user);
        Log.v(TAG, "[DB] user is marked logged out.");
    }


    // update online db when goal is finished for some date
    // will be called from profile and the light exercise
    public static void updateWeeklyCounts(AppDatabase db, int counts) {
        String uid = db.userDao().getUID();
        DatabaseReference dbRoot = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbWeeksRef = dbRoot.child("weeklyOverviews");

        if (uid == null) {
            Log.v(TAG, "[null uid!] can not update weekly counts");
            return;
        }
        DatabaseReference dbWeekRef = dbWeeksRef.child(uid);
        String dateKey = getFirstDayOfWeek();

        dbWeekRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dbWeekRef.child(dateKey).setValue(counts);
                Log.v(TAG, "online counts for week " + dateKey + " is updated to : " + counts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "updateWeeklyCounts onCancelled error: " + error);
            }
        });

    }

    // return the first day of current week in string form yyyy-mm-dd
    public static String getFirstDayOfWeek() {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.DAY_OF_WEEK, 1);
        int month = mCalendar.get(Calendar.MONTH) + 1;
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int year = mCalendar.get(Calendar.YEAR);
        return year + "-" + to2CharString(month) + "-" + to2CharString(day);
    }


    // load profile img from sdcard/WellnessStudio/user_avatar.jpg
    public static boolean loadImageForProfile(ImageView imageView) {
        Bitmap bitmap;
        File filepath = Environment.getExternalStorageDirectory();
        File dir = new File(filepath.getAbsolutePath()
                + "/WellnessStudio/user_avatar.jpg");
        if (dir.exists()) {
            try {
                bitmap =
                        BitmapFactory.decodeFile(dir.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
                // System.out.println(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Log.v(TAG, "in catch, when loading from storage");
                return false;
            }
        } else {
            Log.v(TAG, "no dir, when loading from storage");
            return false;
        }

        return bitmap != null;
    }

}