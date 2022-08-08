package edu.neu.madcourse.wellness_studio;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises_DuringExercise;
import edu.neu.madcourse.wellness_studio.profile.Profile;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import edu.neu.madcourse.wellness_studio.utils.Utils;
import localDatabase.AppDatabase;
import localDatabase.enums.ExerciseSet;
import localDatabase.enums.ExerciseStatus;
import localDatabase.userInfo.User;

public class MainActivity extends AppCompatActivity {
    // test
    private final static String TAG = "main";

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
            Log.v(TAG, "no user exists");
            startActivity(new Intent(MainActivity.this, Greeting.class));
            finish();
            return;
        }

        // user already exists so load user info
        user = UserService.getCurrentUser(db);
        assert user != null;  // should not happen though because we'll return if user is null
        nickname = user.getNickname();

//        // use some test data for current user TODO delete this
//        user.setSleepAlarm("22:50");
//        user.setWakeUpAlarm("08:10");
//        user.setExerciseAlarm("20:00");
//        UserService.updateUserInfo(db, user);
//
//        // test set some dummy data for le TODO delete this
//        String prefix = "2022-07-2";
//        String prefix2 = "2022-06-1";
//        for (int i=0; i<=9; i++) {
//            UserService.createNewLightExercise(db, prefix2+i);
//            UserService.updateExerciseStatus(db, ExerciseStatus.COMPLETED, prefix2+i);
//            UserService.updateExerciseGoalStatus(db, true, prefix2+i);
//        }
//        for (int i=0; i<=7; i++) {
//            UserService.createNewLightExercise(db, prefix+i);
//            UserService.updateExerciseStatus(db, ExerciseStatus.COMPLETED, prefix+i);
//            UserService.updateExerciseGoalStatus(db, true, prefix+i);
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
//        bottomNavigationView.getMenu().findItem(R.id.nav_home).setEnabled(false);
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
                    Log.v(TAG, "Invalid bottom navigation item clicked.");
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
            exerciseGoBtn.setTextSize(10);
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

    // or the nav bar would act very strange
    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

//    @Override
//    public void onBackPressed() {
//        int seletedItemId = bottomNavigationView.getSelectedItemId();
//        if (R.id.nav_home != seletedItemId) {
//            setHomeItem(MainActivity.this);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    public static void setHomeItem(Activity activity) {
//        BottomNavigationView navView = (BottomNavigationView)
//                activity.findViewById(R.id.bottom_navigation);
//        navView.setSelectedItemId(R.id.nav_home);
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