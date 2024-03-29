package edu.neu.madcourse.wellness_studio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises_DuringExercise;
import edu.neu.madcourse.wellness_studio.profile.Profile;
import edu.neu.madcourse.wellness_studio.sleepGoal.WakeupSleepGoal;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import edu.neu.madcourse.wellness_studio.utils.Utils;
import localDatabase.AppDatabase;
import localDatabase.enums.ExerciseSet;
import localDatabase.enums.ExerciseStatus;
import localDatabase.userInfo.User;

public class MainActivity extends AppCompatActivity {
    // test
    private final static String TAG = "mainact";

    // VI
    BottomNavigationView bottomNavigationView;
    Button exerciseGoBtn, sleepGoBtn;
    TextView greetingTV, exerciseStatusTV, exerciseStatusCommentTV, alarmStatusTV;
    TextView dateTV, monthTV, dayOfWeekTV;
    ImageView profileBtn;

    // user and db
    protected User user;
    protected String nickname;

    protected ExerciseStatus currStatus;
    protected ExerciseSet currSet;
    protected String currStatusStr, currStatusComment;
    protected String sleepAlarmStr, wakeupAlarmStr;
    protected AppDatabase db;
    protected String currdate;


    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize db instance
        db = AppDatabase.getDbInstance(this.getApplicationContext());

        // check if user already exists
        // if no, go to greeting screen, finish current activity
        if (!UserService.checkIfUserExists(db)) {
            //Log.v(TAG, "no user exists");
            startActivity(new Intent(MainActivity.this, Greeting.class));
            finish();
            return;
        }

        // user already exists so load user info
        user = UserService.getCurrentUser(db);
        assert user != null;  // should not happen though because we'll return if user is null
        nickname = user.getNickname();

        // test data
//        for (int i = 1; i<32; i++) {
//            if (i == 19 || i == 13) continue;
//            String date = "2022-07-" + to2CharString(i);
//            UserService.updateExerciseGoalStatus(db, true, date);
//        }
//
//        for (int i = 1; i<31; i++) {
//            if (i == 29 || i == 6 || i == 4) continue;
//            String date = "2022-06-" + to2CharString(i);
//            UserService.updateExerciseGoalStatus(db, true, date);
//        }

        // get VI components
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        profileBtn = findViewById(R.id.imageView_profile);
        exerciseGoBtn = findViewById(R.id.button1);
        sleepGoBtn = findViewById(R.id.button2);
        greetingTV = findViewById(R.id.greeting_TV);
        exerciseStatusTV = findViewById(R.id.progressdetail1);
        exerciseStatusCommentTV = findViewById(R.id.progresscomment1);
        alarmStatusTV = findViewById(R.id.progressdetail2);

        dayOfWeekTV = findViewById(R.id.day_of_week_TV);
        dateTV = findViewById(R.id.date_TV);
        monthTV = findViewById(R.id.month_TV);

        // set click listeners for buttons
        sleepGoBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WakeupSleepGoal.class)));
        profileBtn.setOnClickListener(v -> goToProfile());

        // set bottom nav, currently at home so disable home item
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setEnabled(false);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    return false; // should not happen, disabled
                case R.id.nav_exercise:
                    goToLightExercise();
                    return true;
                case R.id.nav_sleep:
                    goToSleepGoal();
                    return true;
                case R.id.nav_leaderboard:
                    goToLeaderboard();
                    return true;
                default:
                    //Log.v(TAG, "Invalid bottom navigation item clicked.");
                    return false;
            }
        });


        // set greeting message in header
        greetingTV.setText("Hello, " + nickname + "!");

        // show current date
        currdate = Utils.getCurrentDate();
        dateTV.setText(currdate.substring(8));
        monthTV.setText(currdate.substring(5,7));
        int dayIdx = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String[] dayArray = getResources().getStringArray(R.array.days_of_week);
        dayOfWeekTV.setText(dayArray[dayIdx-1]);

        // show exercise progress
        // get a le obj for today (UserService should handle the null case)
        currStatus = UserService.getExerciseStatusByDate(db, currdate);
        if (currStatus == null) {
            currStatus = ExerciseStatus.UNKNOWN; // should never happen
        }

        // set text view
        switch (currStatus) {
            case COMPLETED:
                currStatusStr = "Completed";
                currStatusComment = "You did it, congrats!";
               break;
            case NOT_STARTED:
                currStatusStr = "Not Started";
                currStatusComment = "Start working on your goal!";
                break;
            case NOT_FINISHED:
                currStatusStr = "Not Finished";
                currStatusComment = "Keep going!";
                break;
            default:  // handle UNKNOWN, should never happen
                currStatusStr = "No status available.";
                currStatusComment = "Try some exercise?";
                break;
        }

        exerciseStatusTV.setText(currStatusStr);
        exerciseStatusCommentTV.setText(currStatusComment);

        // get current set and set exercise button text
        currSet = UserService.getCurrentSetByDate(db, currdate);
        if (currSet == ExerciseSet.NOT_SELECTED) {
            exerciseGoBtn.setText("GO");
        } else {
            exerciseGoBtn.setText("CONTINUE");
            exerciseGoBtn.setTextSize(9);
        }

        // set exercise go button respond
        exerciseGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currSet) {
                    case NOT_SELECTED:
                        goToLightExercise();
                        break;
                    default:
                        goToCurrentSet();  // if has currSet, go to that set
                        break;
                }
            }
        });


        // show sleep wakeup alarm status
        sleepAlarmStr = UserService.getSleepAlarm(db);
        wakeupAlarmStr = UserService.getWakeupAlarm(db);

        alarmStatusTV.setText(sleepAlarmStr + "  to  " + wakeupAlarmStr);

    }

    // always set selected item, or the nav bar would act very strange
    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        loadProfileImg(profileBtn);
    }

    // check if has permission to access storage (called when load profile picture
    private boolean checkStoragePermission() {
        //Log.v(TAG,"checking permission in main");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            int read = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int write = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return ( read == PackageManager.PERMISSION_GRANTED &&
                    write == PackageManager.PERMISSION_GRANTED );

        } else {
            return Environment.isExternalStorageManager();
        }
    }

    // load profile img from sdcard, if can't load from assets/
    private void loadProfileImg(ImageView imageView) {
        boolean res = UserService.loadImageForProfile(imageView);
        // Log.v(TAG, "res in load ProfileImg in main: " + res);
        if (!res) {
            //Log.v(TAG, "load Image from storage returns false, try assets/");
            try {
                InputStream inputStream = getAssets().open("user_avatar.jpg");
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                imageView.setImageDrawable(drawable);
                //Log.v(TAG, "load from assets.");
            } catch (IOException e) {
                e.printStackTrace();
                //Log.v(TAG, "can not load picture from assets");
            }
        }
    }

    // load image from assets/ to profile image view
    private void loadImageFromAssets(ImageView imageView) {
        try {
            InputStream inputStream = getAssets().open("user_avatar.jpg");
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(drawable);
            //Log.v(TAG, "load from assets.");
        } catch (IOException e) {
            e.printStackTrace();
            //.v(TAG, "can not load picture from assets");
            return;
        }
    }

    // for inserting test data
    // transfer an integer to a 2-char string, add 0 before single digit number
//    private String to2CharString(int num) {
//        return num<10 ? "0"+num : ""+num;
//    }

    // ========   helpers to start new activity  ===================

    private void goToLeaderboard() {
        startActivity(new Intent(MainActivity.this, Leaderboard.class));
    }

    private void goToSleepGoal() {
        startActivity(new Intent(MainActivity.this, WakeupSleepGoal.class));
    }

    private void goToLightExercise() {
        startActivity(new Intent(MainActivity.this, LightExercises.class));
    }

    private void goToCurrentSet() {
        Intent intent = new Intent(this,LightExercises_DuringExercise.class);
        intent.putExtra("exercises_focus_area", currSet);
        startActivity(intent);
    }

    private void goToProfile() {
        startActivity(new Intent(MainActivity.this, Profile.class));
    }

}