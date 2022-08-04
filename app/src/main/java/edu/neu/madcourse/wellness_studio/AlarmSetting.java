package edu.neu.madcourse.wellness_studio;

import android.annotation.SuppressLint;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.w3c.dom.Text;

import edu.neu.madcourse.wellness_studio.friendsList.FriendsList;
import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;

public class AlarmSetting extends AppCompatActivity {
    // test
    private final static String TAG = "alarmsetting";

    TimePicker sleepTimePicker, wakeupTimePicker;
    int sleepAlarmHour, sleepAlarmMin, wakeupAlarmHour, wakeupAlarmMin;
    Button saveButton;
    BottomNavigationView bottomNavigationView;
    public static final String SLEEP_ALARM_KEY_NAME = "sleepAlarmUpdate";
    public static final String WAKEUP_ALARM_KEY_NAME = "wakeupAlarmUpdate";
    String sleepAlarmUpdate, wakeupAlarmUpdate;
    boolean isSave = false;



    @SuppressLint("NonConstantResourceId")
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

        // bottom nav bar
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        // set bottom nav, leaderboard as activated
        bottomNavigationView.setSelectedItemId(R.id.nav_sleep);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    goToHome();
                    return true;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_sleep);
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







    // ========   helpers to start new activity  ===================

    private void goToHome() {
        startActivity(new Intent(AlarmSetting.this, MainActivity.class));
    }

    private void goToLightExercise() {
        startActivity(new Intent(AlarmSetting.this, LightExercises.class));
    }

    private void goToSleepGoal() {
        startActivity(new Intent(AlarmSetting.this, WakeupSleepGoal.class));
    }

    private void goToLeaderboard() {
        startActivity(new Intent(AlarmSetting.this, Leaderboard.class));
    }

}
