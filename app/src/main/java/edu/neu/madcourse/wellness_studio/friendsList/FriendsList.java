package edu.neu.madcourse.wellness_studio.friendsList;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import edu.neu.madcourse.wellness_studio.Greeting;
import edu.neu.madcourse.wellness_studio.Leaderboard;
import edu.neu.madcourse.wellness_studio.LightExercises;
import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.WakeupSleepGoal;

public class FriendsList extends AppCompatActivity {
    ImageButton homeBtn, exerciseBtn, sleepBtn, leaderboardBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        homeBtn = findViewById(R.id.imageButton_home);
        exerciseBtn = findViewById(R.id.imageButton_exercise);
        sleepBtn = findViewById(R.id.imageButton_sleep);
        leaderboardBtn = findViewById(R.id.imageButton_leaderboard);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(FriendsList.this, Greeting.class)));

        exerciseBtn.setOnClickListener(v -> startActivity(new Intent(FriendsList.this, LightExercises.class)));

        sleepBtn.setOnClickListener(v -> startActivity(new Intent(FriendsList.this, WakeupSleepGoal.class)));

        leaderboardBtn.setOnClickListener(v -> startActivity(new Intent(FriendsList.this, Leaderboard.class)));
    }
}
