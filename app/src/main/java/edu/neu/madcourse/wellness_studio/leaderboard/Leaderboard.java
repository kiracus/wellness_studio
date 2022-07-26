package edu.neu.madcourse.wellness_studio.leaderboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import edu.neu.madcourse.wellness_studio.Greeting;
import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.WakeupSleepGoal;
import edu.neu.madcourse.wellness_studio.friendsList.FriendsList;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;

public class Leaderboard extends AppCompatActivity {

    ImageButton homeBtn, exerciseBtn, sleepBtn, leaderboardBtn, friendsList;
    Button refreshBtn;
    TextView currentWeek;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        homeBtn = findViewById(R.id.imageButton_home);
        exerciseBtn = findViewById(R.id.imageButton_exercise);
        sleepBtn = findViewById(R.id.imageButton_sleep);
        leaderboardBtn = findViewById(R.id.imageButton_leaderboard);
        friendsList = findViewById(R.id.go_to_friends);
        currentWeek = findViewById(R.id.currentWeek);
        refreshBtn = findViewById(R.id.refresh_leaderboard);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, Greeting.class)));

        exerciseBtn.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, LightExercises.class)));

        sleepBtn.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, WakeupSleepGoal.class)));

        // leaderboardBtn.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, Leaderboard.class)));
        friendsList.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, FriendsList.class)));

        refreshBtn.setOnClickListener(v -> refreshLeaderboard());

        Calendar mCalendar = Calendar.getInstance();

        // Set first day of week
        mCalendar.set(Calendar.DAY_OF_WEEK, 1);
        int monthStart = mCalendar.get(Calendar.MONTH) + 1;
        int dayStart = mCalendar.get(Calendar.DAY_OF_MONTH);
        int yearStart = mCalendar.get(Calendar.YEAR);

        // Set last day of week
        mCalendar.set(Calendar.DAY_OF_WEEK, 7);
        int monthEnd = mCalendar.get(Calendar.MONTH) + 1;
        int dayEnd = mCalendar.get(Calendar.DAY_OF_MONTH);
        int yearEnd = mCalendar.get(Calendar.YEAR);

        currentWeek.setText("Week from " + monthStart + "-" + dayStart + "-" + yearStart + " to " +
                monthEnd + "-" + dayEnd + "-" + yearEnd);
    }

    public void refreshLeaderboard() {

    }

}