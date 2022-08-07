package edu.neu.madcourse.wellness_studio;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.NotificationManager;
import android.app.NotificationChannel;


import android.annotation.SuppressLint;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.AlarmReceiver;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.profile.Profile;

public class WakeupSleepGoal extends AppCompatActivity {

    // test
    private final static String TAG = "sleep";
    TextView sleepAlarmTV, wakeupAlarmTV, sleepHoursTV;
    ImageView profile, sleepAlarmSetting, wakeupAlarmSetting;
    BottomNavigationView bottomNavigationView;
    protected String sleepAlarmOnOffCheck = "ALARM OFF", wakeAlarmOnOffCheck = "ALARM OFF";
    String sleepAlarmReopenUpdate, wakeupAlarmReopenUpdate, sleepHoursReopenUpdate;

    ActivityResultLauncher<Intent> startForResult;
    SwitchMaterial sleepAlarmSwitch, wakeupAlarmSwitch;
    PendingIntent pendingIntentSleep, pendingIntentWakeUp;
    AlarmManager alarmManagerSleep, alarmManagerWakeup;
    String sleepAlarmUpdate, wakeupAlarmUpdate;
    int sleepAlarmHour = 22, sleepAlarmMin = 30, wakeupAlarmHour = 8, wakeupAlarmMin = 30;
    String isSnooze, isWakeupSensorUse, isSleepSensorUse;
    long wakeupMillis, sleepMillis;



    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup_sleep_goal);
        //notification
        createNotificationChannelSleep();
        createNotificationChannelWakeup();
        profile = findViewById(R.id.imageView_profile);

        sleepAlarmTV = findViewById(R.id.sleep_alarmTime_TV);
        if (sleepAlarmReopenUpdate == null) {
            sleepAlarmTV.setText(sleepAlarmHour + ":" + sleepAlarmMin);
        } else {
            sleepAlarmTV.setText(sleepAlarmReopenUpdate);
        }

        wakeupAlarmTV = findViewById(R.id.wakeup_alarmTime_TV);
        if (wakeupAlarmReopenUpdate == null) {
            wakeupAlarmTV.setText("0"+ wakeupAlarmHour + ":" + sleepAlarmMin);
        } else {
            wakeupAlarmTV.setText(wakeupAlarmReopenUpdate);
        }

        sleepHoursTV = findViewById(R.id.hours_display);
        if (sleepHoursReopenUpdate == null) {
            sleepHoursTV.setText("10 hours, 0 min");
        } else {
            sleepHoursTV.setText(sleepHoursReopenUpdate);
        }

        sleepAlarmSetting = findViewById(R.id.setting_dot);
        wakeupAlarmSetting = findViewById(R.id.wakeup_setting_dot);


        startForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result != null && result.getResultCode() == AlarmSetting.RESULT_OK) {
                            if (result.getData() != null &&
                                    result.getData().getStringExtra(AlarmSetting.SLEEP_ALARM_KEY_NAME) != null ||
                                    result.getData().getStringExtra(AlarmSetting.WAKEUP_ALARM_KEY_NAME) != null ||
                                    result.getData().getStringExtra(AlarmSetting.SNOOZE_VALUE) != null ||
                                    result.getData().getStringExtra(AlarmSetting.SLEEP_ALARM_KEY_NAME) != null ||
                                    result.getData().getStringExtra(AlarmSetting.WAKEUP_ALARM_KEY_NAME) != null){

                                Intent data = result.getData();
                                isSnooze = data.getStringExtra(AlarmSetting.SNOOZE_VALUE);
                                isSleepSensorUse = data.getStringExtra(AlarmSetting.SLEEP_SENSOR_USE);
                                isWakeupSensorUse = data.getStringExtra(AlarmSetting.WAKEUP_SENSOR_USE);


                                if (data.getStringExtra(AlarmSetting.SLEEP_ALARM_KEY_NAME) != null) {
                                    sleepAlarmUpdate = data.getStringExtra(AlarmSetting.SLEEP_ALARM_KEY_NAME);
                                } else {
                                    sleepAlarmUpdate = "22:30";
                                }

                                if (data.getStringExtra(AlarmSetting.WAKEUP_ALARM_KEY_NAME) != null) {
                                    wakeupAlarmUpdate = data.getStringExtra(AlarmSetting.WAKEUP_ALARM_KEY_NAME);
                                } else {
                                    wakeupAlarmUpdate = "08:30";
                                }

                                Log.d("WakeupSleepGoal", "Sleep = " + sleepAlarmUpdate + "Wakeup = " + wakeupAlarmUpdate );

                                if (!TextUtils.isEmpty(sleepAlarmUpdate) && !TextUtils.isEmpty(wakeupAlarmUpdate))
                                    if (sleepAlarmUpdate != null) {
                                        sleepAlarmTV.setText(sleepAlarmUpdate);
                                        sleepAlarmReopenUpdate = sleepAlarmUpdate;
                                        sleepAlarmHour = getHour(sleepAlarmUpdate);
                                        sleepAlarmMin = getMin(sleepAlarmUpdate);
                                        if (sleepAlarmOnOffCheck.equals("ALARM ON")) {
                                            setSleepAlarm(sleepAlarmHour, sleepAlarmMin);
                                        }
                                    }

                                    if (wakeupAlarmUpdate != null) {
                                        wakeupAlarmTV.setText(wakeupAlarmUpdate);
                                        wakeupAlarmReopenUpdate = wakeupAlarmUpdate;
                                        wakeupAlarmHour = getHour(wakeupAlarmUpdate);
                                        wakeupAlarmMin = getMin(wakeupAlarmUpdate);
                                        if (wakeAlarmOnOffCheck.equals("ALARM ON")) {
                                            setWakeupAlarm(wakeupAlarmHour, wakeupAlarmMin);
                                        }
                                    }

                                Log.d("WakeupSleepGoal", "wakeupAlarmUpdate" + wakeupAlarmReopenUpdate);
                                    sleepHoursTV.setText(calculateDiffTime(sleepAlarmUpdate, wakeupAlarmUpdate));
                                    if (sleepAlarmUpdate == null) {
                                        sleepHoursReopenUpdate = calculateDiffTime("22:30", wakeupAlarmUpdate);
                                    } else if (wakeupAlarmUpdate == null) {
                                        sleepHoursReopenUpdate = calculateDiffTime(sleepAlarmUpdate, "08:30");
                                    } else {
                                        sleepHoursReopenUpdate = calculateDiffTime(sleepAlarmUpdate, wakeupAlarmUpdate);
                                    }
                            }
                        } else {
                            return;
                        }

                    }
                });



        sleepAlarmSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WakeupSleepGoal.this, AlarmSetting.class);
//                startActivity(intent);
                startForResult.launch(intent);
            }
        });




        wakeupAlarmSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WakeupSleepGoal.this, AlarmSetting.class);
//                startActivity(intent);
                startForResult.launch(intent);
            }
        });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WakeupSleepGoal.this, Profile.class));
            }
        });

        //switch
        sleepAlarmSwitch = findViewById(R.id.sleep_alarm_on_off);
        sleepAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setSleepAlarm(sleepAlarmHour, sleepAlarmMin);
                    sleepAlarmOnOffCheck = "ALARM ON";
                    Toast.makeText(WakeupSleepGoal.this, "Sleep Alarm is On. Time: " + sleepAlarmHour + ":" + sleepAlarmMin , Toast.LENGTH_SHORT).show();
                } else {
                    cancelSleepAlarm();
                    Toast.makeText(WakeupSleepGoal.this, "Sleep Alarm is Off.", Toast.LENGTH_SHORT).show();
                    sleepAlarmOnOffCheck = "ALARM OFF";
                }
            }
        });
        wakeupAlarmSwitch = findViewById(R.id.wakeup_alarm_on_off);

        wakeupAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setWakeupAlarm(wakeupAlarmHour, wakeupAlarmMin);
                    wakeAlarmOnOffCheck = "ALARM ON";
                } else {
                    cancelWakeupAlarm();
                    wakeAlarmOnOffCheck = "ALARM OFF";
                }
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_sleep);
        bottomNavigationView.getMenu().findItem(R.id.nav_sleep).setEnabled(false);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    goToHome();
                    return true;
                case R.id.nav_exercise:
                    goToLightExercise();
                    return true;
                case R.id.nav_sleep:
                    return false;
                case R.id.nav_leaderboard:
                    goToLeaderboard();
                    return true;
                default:
                    Log.v(TAG, "Invalid bottom navigation item clicked.");
                    return false;
            }
        });


    }



    private void createNotificationChannelSleep() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "alarmAndroidChannelSleep";
            String description = "channel for sleep alarm manager";
            int important = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel1 = new NotificationChannel("sleepAlarmAndroid", name, important);
            channel1.setDescription(description);

            NotificationManager notificationManagerSleep = getSystemService(NotificationManager.class);
            notificationManagerSleep.createNotificationChannel(channel1);
        }
    }

    private void createNotificationChannelWakeup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "alarmAndroidChannelWakeup";
            String description = "channel for wake up alarm manager";
            int important = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel2 = new NotificationChannel("wakeAlarmAndroid", name, important);
            channel2.setDescription(description);

            NotificationManager notificationManagerWakeup = getSystemService(NotificationManager.class);
            notificationManagerWakeup.createNotificationChannel(channel2);
        }
    }

    private int getHour(String s) {
        int time = removeColon(s);
        return time / 100;
    }

    private int getMin(String s) {
        int time = removeColon(s);
        return time % 100;
    }


    private String calculateDiffTime(String sleepAlarm, String wakeupAlarm) {

        int time1 = removeColon(sleepAlarm);
        int time2 = removeColon(wakeupAlarm);


        int sleepHour = time1 / 100;
        int sleepMin = time1 % 100;

        int wakeupHour = time2 / 100;
        int wakeMin = time2 % 100;

        if (sleepHour > wakeupHour) {
            int remainHour = 24 - sleepHour;
            int totalHour = remainHour + wakeupHour;
            int minDiff = wakeMin + (60 - sleepMin);
            if (minDiff >= 60) {
                minDiff = minDiff - 60;
            } else {
                totalHour--;
            }

            if (minDiff <= 1 && totalHour <= 1 ) {
                return String.valueOf(Math.abs(totalHour)) + " hour, " + String.valueOf(Math.abs(minDiff)) + " min";
            } else if (minDiff <= 1 && totalHour > 1){
                return String.valueOf(Math.abs(totalHour)) + " hours, " + String.valueOf(Math.abs(minDiff)) + " min";
            } else if (minDiff > 1 && totalHour <= 1) {
                return String.valueOf(Math.abs(totalHour)) + " hour, " + String.valueOf(Math.abs(minDiff)) + " mins";
            } else {
                return String.valueOf(Math.abs(totalHour)) + " hours, " + String.valueOf(Math.abs(minDiff)) + " mins";
            }

        } else {
            int hourDiff = time2 / 100 - time1 / 100 - 1;
            int minDiff = wakeMin + (60 - sleepMin);
            if (minDiff >= 60) {
                hourDiff++;
                minDiff = minDiff - 60;
            }
            if (minDiff <= 1 && hourDiff <= 1 ) {
                return String.valueOf(Math.abs(hourDiff)) + " hour, " + String.valueOf(Math.abs(minDiff)) + " min";
            } else if (minDiff <= 1 && hourDiff > 1){
                return String.valueOf(Math.abs(hourDiff)) + " hours, " + String.valueOf(Math.abs(minDiff)) + " min";
            } else if (minDiff > 1 && hourDiff <= 1) {
                return String.valueOf(Math.abs(hourDiff)) + " hour, " + String.valueOf(Math.abs(minDiff)) + " mins";
            } else {
                return String.valueOf(Math.abs(hourDiff)) + " hours, " + String.valueOf(Math.abs(minDiff)) + " mins";
            }


        }
    }


    private int removeColon(String s) {
        if (s.length() == 4)
            s = s.substring(0,1) + s.substring(2);

        if (s.length() == 5)
            s = s.substring(0,2) + s.substring(3);

        return Integer.valueOf(s);
    }


    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_sleep);
    }

    // ========   helpers to start new activity  ===================

    private void goToHome() {
        startActivity(new Intent(WakeupSleepGoal.this, MainActivity.class));
    }

    private void goToLightExercise() {
        startActivity(new Intent(WakeupSleepGoal.this, LightExercises.class));
    }

    private void goToSleepGoal() {
        startActivity(new Intent(WakeupSleepGoal.this, WakeupSleepGoal.class));
    }

    private void goToLeaderboard() {
        startActivity(new Intent(WakeupSleepGoal.this, Leaderboard.class));
    }



    public void setSleepAlarm(int hour, int min) {
        Intent intent = new Intent(this, AlarmSleepReceiver.class);
        long millis = convertHourAndMinToMilliSecondsSleep(hour, min);
        sleepMillis = millis;

        alarmManagerSleep = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        pendingIntentSleep = PendingIntent.getBroadcast(this,0,intent,0);
        Log.d("myApp","sleep milis" + millis);
        alarmManagerSleep.setExact(AlarmManager.RTC_WAKEUP,millis,pendingIntentSleep);
        Toast.makeText(getApplicationContext(),"Sleep Alarm is on Time: " + hour + ":" + min,Toast.LENGTH_SHORT).show();
    }

    private void setWakeupAlarm(int wakeupAlarmHour, int wakeupAlarmMin) {
        Intent intent = new Intent(this, AlarmWakeupReceiver.class);
        long millis = convertHourAndMinToMilliSecondsWakeup(wakeupAlarmHour, wakeupAlarmMin);
        wakeupMillis = millis;

        alarmManagerWakeup = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        pendingIntentWakeUp = PendingIntent.getBroadcast(this,1,intent,0);
        Log.d("myApp","wakeup milis" + millis);
        alarmManagerWakeup.setExact(AlarmManager.RTC_WAKEUP,millis,pendingIntentWakeUp);
        Toast.makeText(getApplicationContext(),"Wakeup Alarm is on Time: " + wakeupAlarmHour + ":" + wakeupAlarmMin,Toast.LENGTH_SHORT).show();
    }

    private long convertHourAndMinToMilliSecondsSleep(int hour, int min) {

        Calendar calendarSleep = Calendar.getInstance();
        calendarSleep.setTimeInMillis(System.currentTimeMillis());
        calendarSleep.set(Calendar.HOUR_OF_DAY, hour);
        calendarSleep.set(Calendar.MINUTE, min);
        calendarSleep.set(Calendar.SECOND, 0);
        calendarSleep.set(Calendar.MILLISECOND, 0);

        // if alarm time has already passed, increment day by 1
        if (calendarSleep.getTimeInMillis() <= System.currentTimeMillis()) {
            calendarSleep.set(Calendar.DAY_OF_MONTH, calendarSleep.get(Calendar.DAY_OF_MONTH) + 1);
        }
        Log.d("myApp","sleep calendar" + calendarSleep.getTime());
        long millis = calendarSleep.getTimeInMillis();
        return millis;


    }

    private long convertHourAndMinToMilliSecondsWakeup(int hour, int min) {

        Calendar calendarWakeup = Calendar.getInstance();
        calendarWakeup.setTimeInMillis(System.currentTimeMillis());
        calendarWakeup.set(Calendar.HOUR_OF_DAY, hour);
        calendarWakeup.set(Calendar.MINUTE, min);
        calendarWakeup.set(Calendar.SECOND, 0);
        calendarWakeup.set(Calendar.MILLISECOND, 0);

        // if alarm time has already passed, increment day by 1
        if (calendarWakeup.getTimeInMillis() <= System.currentTimeMillis()) {
            calendarWakeup.set(Calendar.DAY_OF_MONTH, calendarWakeup.get(Calendar.DAY_OF_MONTH) + 1);
        }
        Log.d("myApp","sleep calendar" + calendarWakeup.getTime());
        long millis = calendarWakeup.getTimeInMillis();
        return millis;
    }

    public void cancelSleepAlarm() {
        Intent intent = new Intent(this,AlarmReceiver.class);
        pendingIntentSleep = PendingIntent.getBroadcast(this,0,intent,0);
        if(alarmManagerSleep == null) {
            alarmManagerSleep = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        alarmManagerSleep.cancel(pendingIntentSleep);
        Toast.makeText(getApplicationContext(),"Sleep Alarm is off",Toast.LENGTH_SHORT).show();
    }

    public void cancelWakeupAlarm() {
        Intent intent = new Intent(this,AlarmReceiver.class);
        pendingIntentWakeUp = PendingIntent.getBroadcast(this,1,intent,0);
        if(alarmManagerWakeup == null) {
            alarmManagerWakeup = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        alarmManagerWakeup.cancel(pendingIntentWakeUp);
        Toast.makeText(getApplicationContext(),"Wakeup Alarm is off",Toast.LENGTH_SHORT).show();
    }

//    private void cancelWakeupSensor() {
//    }
//
//    private void startWakeupSensor() {
//    }
//
//    private void cancelSleepSensor() {
//    }
//
//    private void startSleepSensor() {
//        snoozeAlarm();
//    }
//
//    private void snoozeAlarm() {
//
//    }
//
//    private void cancelSnooze() {
//
//    }
//
//    private void startSnooze() {
//
//
//    }
}