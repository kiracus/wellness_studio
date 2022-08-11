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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.w3c.dom.Text;


import edu.neu.madcourse.wellness_studio.friendsList.FriendsList;

import java.util.Locale;

import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import localDatabase.AppDatabase;

public class AlarmSetting extends AppCompatActivity {
    // test
    private final static String TAG = "alarmSetting";

//    TimePicker sleepTimePicker, wakeupTimePicker;
    int sleepAlarmHour = 22, sleepAlarmMin = 30, wakeupAlarmHour = 8, wakeupAlarmMin = 30;
    ImageButton saveButton, cancelButton;
    BottomNavigationView bottomNavigationView;
    public static final String SLEEP_ALARM_KEY_NAME = "sleepAlarmUpdate";
    public static final String WAKEUP_ALARM_KEY_NAME = "wakeupAlarmUpdate";
    public static final String SNOOZE_VALUE = "snoozeValue";
    public static final String WAKEUP_SENSOR_USE = "wakeupSensorUse";
    public static final String SLEEP_SENSOR_USE = "sleepSensorUse";
    String sleepAlarmUpdate, wakeupAlarmUpdate;
    String sleepAlarmReopenUpdate, wakeupAlarmReopenUpdate;
    TextView sleepAlarmChangeTV, wakeupAlarmChangeTV;
    SwitchMaterial  snoozeBtn, allowWakeupSensorUseBtn;
    SwitchMaterial allowSleepSensorUseBtn;
    Spinner alarmTypeSpinner, stopAlarmSpinner;
    boolean isSave = false, isSnooze = false, isWakeupSensorUse = false, isSleepSensorUse = false;
    String sleepSensorUse = "OFF", snoozeUse = "OFF", wakeupSensorUse = "OFF";



    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_alarm_time);
        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());

        saveButton = findViewById(R.id.change_save_btn);
        cancelButton = findViewById(R.id.imageButton_cancel);

        cancelButton.setOnClickListener(v -> goToSleepGoal());
        sleepAlarmChangeTV = findViewById(R.id.sleep_alarm_change_time_TV);

        sleepAlarmChangeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popSleepTimePicker(v);
            }
        });
        sleepAlarmChangeTV.setText(UserService.getSleepAlarm(db));

        wakeupAlarmChangeTV = findViewById(R.id.wakeup_alarm_change_time_TV);
        wakeupAlarmChangeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWakeTimePicker(v);
            }
        });
        wakeupAlarmChangeTV.setText(UserService.getWakeupAlarm(db));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSave = !isSave;
                if (isSave) {

                    Intent intent = new Intent();
                    intent.putExtra(SLEEP_ALARM_KEY_NAME, sleepAlarmUpdate);
                    intent.putExtra(WAKEUP_ALARM_KEY_NAME, wakeupAlarmUpdate);
                    intent.putExtra(SNOOZE_VALUE, snoozeUse);
                    intent.putExtra(WAKEUP_SENSOR_USE, wakeupSensorUse);
                    intent.putExtra(SLEEP_SENSOR_USE, sleepSensorUse);
                    setResult(RESULT_OK, intent);
                    finish();
                    Toast.makeText(AlarmSetting.this, "save the changes", Toast.LENGTH_SHORT).show();
                    isSave = !isSave;
                }

            }
        });


        alarmTypeSpinner = findViewById(R.id.alarm_type_spinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.alarm_type_array, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alarmTypeSpinner.setAdapter(typeAdapter);
        alarmTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String newItem = alarmTypeSpinner.getSelectedItem().toString();
//                Toast.makeText(AlarmSetting.this, "you selected:" + newItem, Toast.LENGTH_SHORT).show();
                typeAdapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        stopAlarmSpinner = findViewById(R.id.to_stop_alarm_spinner);
        ArrayAdapter<CharSequence> stopAdapter = ArrayAdapter.createFromResource(this,
                R.array.stop_alarm_type_array, android.R.layout.simple_spinner_item);
        stopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stopAlarmSpinner.setAdapter(stopAdapter);

        stopAlarmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String stopNewItem = parent.getItemAtPosition(position).toString();
//                Toast.makeText(AlarmSetting.this, "you selected:" + stopNewItem, Toast.LENGTH_SHORT).show();
                stopAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //snooze
        snoozeBtn = findViewById(R.id.snooze_switch);
        snoozeBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    isSnooze = !isSnooze;
                    if (isSnooze) {
                        snoozeUse = "ON";
                    } else {
                        snoozeUse = "OFF";
                    }
                }
            }
        });

        //wakeup sensor
        allowWakeupSensorUseBtn = findViewById(R.id.sensor_use_switch);
        allowWakeupSensorUseBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isWakeupSensorUse = !isWakeupSensorUse;
                    if (isWakeupSensorUse) {
                        wakeupSensorUse = "ON";
                    } else {
                        wakeupSensorUse = "OFF";
                    }
                }
            }
        });


        //sleep sensor
        allowSleepSensorUseBtn = findViewById(R.id.sleep_reminder_allow_sensor_switch);
        allowSleepSensorUseBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isSleepSensorUse = !isSleepSensorUse;
                    if (isSleepSensorUse) {
                        sleepSensorUse = "ON";
                    } else {
                        sleepSensorUse = "OFF";
                    }
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
