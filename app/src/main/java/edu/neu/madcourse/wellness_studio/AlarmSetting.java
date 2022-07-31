package edu.neu.madcourse.wellness_studio;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;

public class AlarmSetting extends AppCompatActivity {

    TimePicker sleepTimePicker, wakeupTimePicker;
    int sleepAlarmHour, sleepAlarmMin, wakeupAlarmHour, wakeupAlarmMin;
    Button saveButton;
    ImageButton homeBtn, exerciseBtn, sleepBtn, leaderboardBtn;
    public static final String SLEEP_ALARM_KEY_NAME = "sleepAlarmUpdate";
    public static final String WAKEUP_ALARM_KEY_NAME = "wakeupAlarmUpdate";
    String sleepAlarmUpdate, wakeupAlarmUpdate;
    boolean isSave = false;



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
                isSave = !isSave;
                if (isSave) {
                    saveChanges(v);

                    //update Alarm
                    Log.d("AlarmSetting", "save button" + "wakeup" + wakeupAlarmUpdate + " " + "sleep" + sleepAlarmUpdate);

                    Intent intent = new Intent();
                    intent.putExtra(SLEEP_ALARM_KEY_NAME, sleepAlarmUpdate);
                    intent.putExtra(WAKEUP_ALARM_KEY_NAME, wakeupAlarmUpdate);
                    setResult(RESULT_OK, intent);
                    finish();
                    Toast.makeText(AlarmSetting.this, "save the changes", Toast.LENGTH_SHORT).show();
                    isSave = !isSave;
                }

            }
        });

        //Home UI buttons
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

    public void getCurrentSleepAlarm(View view) {
//        sleepTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//            @Override
//            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//                sleepAlarmHour = hourOfDay + "";
//                sleepAlarmMin = minute + "";
//                sleepAlarmUpdate = sleepAlarmHour + ":" + sleepAlarmMin;
//                Log.d("AlarmSetting", sleepAlarmUpdate);
//
//            }
//        });


            sleepAlarmHour = sleepTimePicker.getHour();
            sleepAlarmMin = sleepTimePicker.getMinute();
            sleepAlarmUpdate = sleepAlarmHour + ":" + sleepAlarmMin;
            Log.d("AlarmSetting", sleepAlarmHour + ":" + sleepAlarmMin);
    }

    public void getCurrentWakeupAlarm(View view) {
//        wakeupTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//            @Override
//            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
//                wakeupAlarmHour = hourOfDay + "";
//                wakeupAlarmMin = minute + "";
//                wakeupAlarmUpdate = wakeupAlarmHour + ":" + wakeupAlarmMin;
//                Log.d("AlarmSetting", wakeupAlarmUpdate);
//            }
//        });

        wakeupAlarmHour = wakeupTimePicker.getHour();
        wakeupAlarmMin = wakeupTimePicker.getMinute();
        wakeupAlarmUpdate = wakeupAlarmHour + ":" + wakeupAlarmMin;
        Log.d("AlarmSetting", wakeupAlarmHour + ":" + wakeupAlarmMin);
    }


    public void saveChanges(View v) {
        getCurrentSleepAlarm(v);
        getCurrentWakeupAlarm(v);
    }








}
