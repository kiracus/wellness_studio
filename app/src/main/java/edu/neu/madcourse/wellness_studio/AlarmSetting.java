package edu.neu.madcourse.wellness_studio;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
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

import java.util.Locale;

import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;

public class AlarmSetting extends AppCompatActivity {
    // test
    private final static String TAG = "alarmSetting";

//    TimePicker sleepTimePicker, wakeupTimePicker;
    int sleepAlarmHour = 22, sleepAlarmMin = 30, wakeupAlarmHour = 8, wakeupAlarmMin = 30;
    ImageButton saveButton;
    BottomNavigationView bottomNavigationView;
    public static final String SLEEP_ALARM_KEY_NAME = "sleepAlarmUpdate";
    public static final String WAKEUP_ALARM_KEY_NAME = "wakeupAlarmUpdate";
    String sleepAlarmUpdate, wakeupAlarmUpdate;
    String sleepAlarmReopenUpdate, wakeupAlarmReopenUpdate;
    TextView sleepAlarmChangeTV, wakeupAlarmChangeTV;
    static boolean isSave = false;



    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_alarm_time);

        saveButton = findViewById(R.id.change_save_btn);
        sleepAlarmChangeTV = findViewById(R.id.sleep_alarm_change_time_TV);

        sleepAlarmChangeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popSleepTimePicker(v);
            }
        });

        if (sleepAlarmReopenUpdate == null) {
            sleepAlarmChangeTV.setText(sleepAlarmHour + ":" + sleepAlarmMin);
        } else {
            sleepAlarmChangeTV.setText(sleepAlarmReopenUpdate);
        }



        wakeupAlarmChangeTV = findViewById(R.id.wakeup_alarm_change_time_TV);

        wakeupAlarmChangeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWakeTimePicker(v);
            }
        });

        if (wakeupAlarmReopenUpdate == null) {
            wakeupAlarmChangeTV.setText("0"+ wakeupAlarmHour + ":" + sleepAlarmMin);
        } else {
            wakeupAlarmChangeTV.setText(wakeupAlarmReopenUpdate);
        }



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



    private void goToLightExercise() {
        startActivity(new Intent(AlarmSetting.this, LightExercises.class));
    }



    public void saveChanges(View v) {

    }



    public void popSleepTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                sleepAlarmHour = hourOfDay;
                sleepAlarmMin = minute;
                String time  = String.format(Locale.getDefault(),"%02d:%02d",sleepAlarmHour,sleepAlarmMin);
                sleepAlarmChangeTV.setText(time);
                sleepAlarmUpdate = time;
                sleepAlarmReopenUpdate = time;

            }
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,style,onTimeSetListener,sleepAlarmHour,sleepAlarmMin,true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    public void popWakeTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                wakeupAlarmHour = hourOfDay;
                wakeupAlarmMin = minute;
                String time  = String.format(Locale.getDefault(),"%02d:%02d",wakeupAlarmHour,wakeupAlarmMin);
                wakeupAlarmChangeTV.setText(time);
                wakeupAlarmUpdate = time;
                wakeupAlarmReopenUpdate = time;

            }
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,style,onTimeSetListener,wakeupAlarmHour,wakeupAlarmMin,true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }




    // ========   helpers to start new activity  ===================

    private void goToHome() {
        startActivity(new Intent(AlarmSetting.this, MainActivity.class));
    }


    private void goToSleepGoal() {
        startActivity(new Intent(AlarmSetting.this, WakeupSleepGoal.class));
    }

    private void goToLeaderboard() {
        startActivity(new Intent(AlarmSetting.this, Leaderboard.class));
    }

}
