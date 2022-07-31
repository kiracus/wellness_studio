package edu.neu.madcourse.wellness_studio;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Time;

import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;

public class AlarmSetting extends AppCompatActivity {

    TimePicker sleepTimePicker, wakeupTimePicker;
    static String sleepAlarmHour, sleepAlarmMin, wakeupAlarmHour, wakeupAlarmMin;
    Button saveButton;
    ImageButton homeBtn, exerciseBtn, sleepBtn, leaderboardBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_alarm_time);

        sleepTimePicker = (TimePicker) findViewById(R.id.sleep_timePicker);
        sleepTimePicker.setIs24HourView(true);

        wakeupTimePicker = (TimePicker) findViewById(R.id.wakeup_timePicker);
        wakeupTimePicker.setIs24HourView(true);

        saveButton = findViewById(R.id.change_save_btn);



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(AlarmSetting.this, "save the changes", Toast.LENGTH_SHORT).show();
                saveChanges();
                Intent intent = new Intent(AlarmSetting.this, WakeupSleepGoal.class);

            }
        });


        homeBtn = findViewById(R.id.imageButton_home);
        exerciseBtn = findViewById(R.id.imageButton_exercise);
        sleepBtn = findViewById(R.id.imageButton_sleep);
        leaderboardBtn = findViewById(R.id.imageButton_leaderboard);
        homeBtn.setOnClickListener(v -> startActivity(new Intent(AlarmSetting.this, Greeting.class)));

        // set click listeners for buttons
        exerciseBtn.setOnClickListener(v -> goToLightExercise());
        //exerciseGoBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LightExercises.class)));
        sleepBtn.setOnClickListener(v -> startActivity(new Intent(AlarmSetting.this, WakeupSleepGoal.class)));
        leaderboardBtn.setOnClickListener(v -> startActivity(new Intent(AlarmSetting.this, Leaderboard.class)));


    }


    private void goToLightExercise() {
        startActivity(new Intent(AlarmSetting.this, LightExercises.class));
    }

    public void getCurrentSleepAlarm() {
        sleepTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                sleepAlarmHour = hourOfDay + "";
                sleepAlarmMin = minute + "";

            }
        });

    }

    public void getCurrentWakeupAlarm() {
        wakeupTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                wakeupAlarmHour = hourOfDay + "";
                wakeupAlarmMin = minute + "";
            }
        });


    }

    public void saveChanges() {
        getCurrentSleepAlarm();
        getCurrentWakeupAlarm();

    }






}
