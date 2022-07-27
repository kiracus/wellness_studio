package edu.neu.madcourse.wellness_studio;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import localDatabase.AppDatabase;
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
    protected LightExercise lightExercise;
    protected ExerciseStatus currStatus;
    protected String currStatusStr, currStatusComment;
    protected AppDatabase db;


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

        Log.v(TAG, "if block passed");

        // user already exists so load user info
        user = UserService.getCurrentUser(db);
        nickname = user.getNickname();

        // use some test data for current user
        user.setSleepAlarm("22:50");
        user.setWakeUpAlarm("08:10");
        user.setExerciseAlarm("20:00");


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

        // set greeting message in header
        greetingTV.setText("Hello, " + nickname + " !");


        // for test only, home now directs to greeting TODO: home button does nothing
        homeBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Greeting.class)));

        // set click listeners for buttons
        exerciseBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LightExercises.class)));
        exerciseGoBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LightExercises.class)));

        sleepBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WakeupSleepGoal.class)));
        sleepGoBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WakeupSleepGoal.class)));

        leaderboardBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Leaderboard.class)));

        profileBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Profile.class)));


        // set light exercise status
        currStatus = UserService.getCurrentExerciseStatus(db);

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
            default:
                currStatusStr = "No status available.";
                currStatusComment = "Try some exercise?";
                break;
        }

        exerciseStatusTV.setText(currStatusStr);
        exerciseStatusCommentTV.setText(currStatusComment);


//        // for test only
//        profileBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, Profile_With_Local_DB_Example.class));
//            }
//        });
    }
}