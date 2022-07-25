package edu.neu.madcourse.wellness_studio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import edu.neu.madcourse.wellness_studio.friendsList.FriendsList;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;

public class Leaderboard extends AppCompatActivity {

    ImageButton homeBtn, exerciseBtn, sleepBtn, leaderboardBtn, friendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        homeBtn = findViewById(R.id.imageButton_home);
        exerciseBtn = findViewById(R.id.imageButton_exercise);
        sleepBtn = findViewById(R.id.imageButton_sleep);
        leaderboardBtn = findViewById(R.id.imageButton_leaderboard);
        friendsList = findViewById(R.id.go_to_friends);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, Greeting.class)));

        exerciseBtn.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, LightExercises.class)));

        sleepBtn.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, WakeupSleepGoal.class)));

        // leaderboardBtn.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, Leaderboard.class)));
        friendsList.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, FriendsList.class)));

    }
}