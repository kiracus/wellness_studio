package edu.neu.madcourse.wellness_studio;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises_DuringExercise;
import edu.neu.madcourse.wellness_studio.profile.Profile;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import edu.neu.madcourse.wellness_studio.utils.Utils;
import localDatabase.AppDatabase;
import localDatabase.enums.ExerciseSet;
import localDatabase.enums.ExerciseStatus;
import localDatabase.lightExercise.LightExercise;
import localDatabase.userInfo.User;

public class MainActivity extends AppCompatActivity {
    // test
    private final static String TAG = "main";

    // VI
    ImageButton homeBtn, exerciseBtn, sleepBtn, leaderboardBtn, profileBtn;
    Button exerciseGoBtn, sleepGoBtn;
    TextView greetingTV, exerciseStatusTV, exerciseStatusCommentTV, alarmStatusTV;

    // user and db
    protected User user;
    protected String nickname;

    protected ExerciseStatus currStatus;
    protected ExerciseSet currSet;
    protected String currStatusStr, currStatusComment;
    protected String sleepAlarmStr, wakeupAlarmStr;
    protected AppDatabase db;
    protected String currdate;


    @SuppressLint("SetTextI18n")
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

        // use some test data for current user TODO delete this
        user.setSleepAlarm("22:50");
        user.setWakeUpAlarm("08:10");
        user.setExerciseAlarm("20:00");
        UserService.updateUserInfo(db, user);

        // test set some dummy data for le TODO delete this
        String prefix = "2022-07-2";
        String prefix2 = "2022-06-1";
        for (int i=0; i<=9; i++) {
            UserService.createNewLightExercise(db, prefix2+i);
            UserService.updateExerciseStatus(db, ExerciseStatus.COMPLETED, prefix2+i);
            UserService.updateExerciseGoalStatus(db, true, prefix2+i);
        }
        for (int i=0; i<=7; i++) {
            UserService.createNewLightExercise(db, prefix+i);
            UserService.updateExerciseStatus(db, ExerciseStatus.COMPLETED, prefix+i);
            UserService.updateExerciseGoalStatus(db, true, prefix+i);
        }


        // get VI components
        homeBtn = findViewById(R.id.imageButton_home);
        exerciseBtn = findViewById(R.id.imageButton_exercise);
        sleepBtn = findViewById(R.id.imageButton_sleep);
        leaderboardBtn = findViewById(R.id.imageButton_leaderboard);
        profileBtn = findViewById(R.id.imageButton_profile);
        exerciseGoBtn = findViewById(R.id.button1);
        sleepGoBtn = findViewById(R.id.button2);
        greetingTV = findViewById(R.id.greeting_TV);
        exerciseStatusTV = findViewById(R.id.progressdetail1);
        exerciseStatusCommentTV = findViewById(R.id.progresscomment1);
        alarmStatusTV = findViewById(R.id.progressdetail2);

        // for test only, home now directs to greeting TODO: home button does nothing
        homeBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Greeting.class)));

        // set click listeners for buttons
        exerciseBtn.setOnClickListener(v -> goToLightExercise());
        //exerciseGoBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LightExercises.class)));
        sleepBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WakeupSleepGoal.class)));
        sleepGoBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WakeupSleepGoal.class)));
        leaderboardBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Leaderboard.class)));
        profileBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Profile.class)));

        // set greeting message in header
        greetingTV.setText("Hello, " + nickname + " !");

        // show current date
        currdate = Utils.getCurrentDate();

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
        switch (currSet) {
            case NOT_SELECTED:
                exerciseGoBtn.setText("GO");
                break;
            default:
                exerciseGoBtn.setText("CONTINUE");
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


    private void goToLightExercise() {
        startActivity(new Intent(MainActivity.this, LightExercises.class));
    }

    private void goToCurrentSet() {
        Intent intent = new Intent(this,LightExercises_DuringExercise.class);
        intent.putExtra("exercises_focus_area", currSet);
        startActivity(intent);
    }

}