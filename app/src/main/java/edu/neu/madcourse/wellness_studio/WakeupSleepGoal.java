package edu.neu.madcourse.wellness_studio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Calendar;

import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;


public class WakeupSleepGoal extends AppCompatActivity {
    Button sleepAlarmOnOffBtn, wakeupAlarmOnOffBtn;
    TextView sleepAlarmTV, wakeupAlarmTV, sleepHoursTV, sleepAlarmOnTV, wakeupAlarmOnTV;
    ImageView profile, sleepAlarmSetting, wakeupAlarmSetting;
    ImageButton homeBtn, exerciseBtn, sleepBtn, leaderboardBtn;
    String sleepAlarmOnOffCheck = "ALARM OFF", wakeAlarmOnOffCheck = "ALARM OFF";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup_sleep_goal);

        profile = findViewById(R.id.profile_image);
        sleepAlarmOnOffBtn = findViewById(R.id.alarm_on_off);
        wakeupAlarmOnOffBtn = findViewById(R.id.wakeup_alarm_on_off);

        sleepAlarmTV = findViewById(R.id.sleep_alarmTime_TV);
        wakeupAlarmTV = findViewById(R.id.wakeup_alarmTime_TV);

        sleepHoursTV = findViewById(R.id.hours_display);

        sleepAlarmSetting = findViewById(R.id.setting_dot);
        wakeupAlarmSetting = findViewById(R.id.wakeup_setting_dot);

        sleepAlarmOnTV = findViewById(R.id.alarm_on_TV);
        wakeupAlarmOnTV = findViewById(R.id.wakeup_alarm_on_TV);


        sleepAlarmSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WakeupSleepGoal.this, AlarmSetting.class);
                startActivity(intent);
            }
        });

        wakeupAlarmSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WakeupSleepGoal.this, AlarmSetting.class);
                startActivity(intent);
            }
        });

        sleepAlarmOnOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSleepToggleClicked(v);
            }
        });

        wakeupAlarmOnOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWakeupToggleClicked(v);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WakeupSleepGoal.this, Profile.class));
            }
        });



        homeBtn = findViewById(R.id.imageButton_home);
        exerciseBtn = findViewById(R.id.imageButton_exercise);
        sleepBtn = findViewById(R.id.imageButton_sleep);
        leaderboardBtn = findViewById(R.id.imageButton_leaderboard);
        homeBtn.setOnClickListener(v -> startActivity(new Intent(WakeupSleepGoal.this, Greeting.class)));

        // set click listeners for buttons
        exerciseBtn.setOnClickListener(v -> goToLightExercise());
        //exerciseGoBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LightExercises.class)));
        sleepBtn.setOnClickListener(v -> startActivity(new Intent(WakeupSleepGoal.this, WakeupSleepGoal.class)));
        leaderboardBtn.setOnClickListener(v -> startActivity(new Intent(WakeupSleepGoal.this, Leaderboard.class)));



    }

    private void goToLightExercise() {
        startActivity(new Intent(WakeupSleepGoal.this, LightExercises.class));
    }

    private void onSleepToggleClicked(View view) {
        boolean on = ((ToggleButton)view).isChecked();

        if (on) {
            sleepAlarmOnTV.setText("ALARM ON");
            sleepAlarmOnOffCheck = "ALARM ON";
        } else {
            sleepAlarmOnTV.setText("ALARM OFF");
            sleepAlarmOnOffCheck = "ALARM OFF";
        }
    }

    private void onWakeupToggleClicked(View view) {
        boolean on = ((ToggleButton)view).isChecked();

        if (on) {
            wakeupAlarmOnTV.setText("ALARM ON");
            wakeAlarmOnOffCheck = "ALARM ON";

        } else {
            wakeupAlarmOnTV.setText("ALARM OFF");
            wakeAlarmOnOffCheck = "ALARM OFF";
        }
    }

    public void updateAlarmTextView() {
                wakeupAlarmTV.setText(AlarmSetting.wakeupAlarmHour + ":" + AlarmSetting.wakeupAlarmMin);
                sleepAlarmTV.setText(AlarmSetting.sleepAlarmHour + ":" + AlarmSetting.sleepAlarmMin);
    }

}