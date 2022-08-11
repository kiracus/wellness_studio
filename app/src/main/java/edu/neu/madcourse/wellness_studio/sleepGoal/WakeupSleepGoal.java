package edu.neu.madcourse.wellness_studio.sleepGoal;

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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.graphics.drawable.Drawable;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import edu.neu.madcourse.wellness_studio.MainActivity;
import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.AlarmReceiver;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.profile.Profile;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import localDatabase.AppDatabase;

public class WakeupSleepGoal extends AppCompatActivity {

    // test
    private final static String TAG = "sleep";
    TextView sleepAlarmTV, wakeupAlarmTV, sleepHoursTV;
    ImageView profile, sleepAlarmSetting, wakeupAlarmSetting;
    BottomNavigationView bottomNavigationView;

    SwitchMaterial sleepAlarmSwitch, wakeupAlarmSwitch;
    PendingIntent pendingIntentSleep, pendingIntentWakeUp;
    AlarmManager alarmManagerSleep, alarmManagerWakeup;
    String sleepAlarmUpdate, wakeupAlarmUpdate;
    int sleepAlarmHour, sleepAlarmMin, wakeupAlarmHour, wakeupAlarmMin;
    long wakeupMillis, sleepMillis;
    boolean isUserAsleep = false;
    boolean isWakeupAlarmOn, isSleepAlarmOn;
    private SensorManager mSensorManager;
    private Sensor sensor;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup_sleep_goal);
        //notification
        createNotificationChannelSleep();
        createNotificationChannelWakeup();

        AppDatabase db = AppDatabase.getDbInstance(this.getApplicationContext());
        Boolean sleepAlarmOn = UserService.getSleepAlarmON(db);
        Boolean wakeupAlarmOn = UserService.getWakeupAlarmON(db);
        sleepAlarmUpdate = UserService.getSleepAlarm(db);
        wakeupAlarmUpdate = UserService.getWakeupAlarm(db);

        profile = findViewById(R.id.imageView_profile);

        sleepAlarmTV = findViewById(R.id.sleep_alarmTime_TV);
        sleepAlarmTV.setText(sleepAlarmUpdate);


        wakeupAlarmTV = findViewById(R.id.wakeup_alarmTime_TV);
        wakeupAlarmTV.setText(wakeupAlarmUpdate);


        sleepHoursTV = findViewById(R.id.hours_display);

        sleepAlarmSetting = findViewById(R.id.setting_dot);
        wakeupAlarmSetting = findViewById(R.id.wakeup_setting_dot);

        if (!sleepAlarmUpdate.equals("--:--") && !wakeupAlarmUpdate.equals("--:--")) {
            sleepHoursTV.setText((calculateDiffTime(sleepAlarmUpdate, wakeupAlarmUpdate)));
            wakeupAlarmHour = getHour(wakeupAlarmUpdate);
            wakeupAlarmMin = getMin(wakeupAlarmUpdate);
            sleepAlarmHour = getHour(sleepAlarmUpdate);
            sleepAlarmMin = getMin(sleepAlarmUpdate);
            if (sleepAlarmOn) {
                setSleepAlarm(sleepAlarmHour, sleepAlarmMin);
            }
            if (wakeupAlarmOn) {
                setWakeupAlarm(wakeupAlarmHour, wakeupAlarmMin);
            }
        } else {
            sleepHoursTV.setText("0 hour, 0 min");
        }


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


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WakeupSleepGoal.this, Profile.class));
            }
        });

        //switch
        sleepAlarmSwitch = findViewById(R.id.sleep_alarm_on_off);
        sleepAlarmSwitch.setChecked(sleepAlarmOn);
        sleepAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (UserService.getSleepAlarm(db) != "--:--" && UserService.getWakeupAlarm(db) != "--:--") {
                        isSleepAlarmOn = true;
                        setSleepAlarm(sleepAlarmHour, sleepAlarmMin);
                        UserService.updateSleepAlarmOn(db,isSleepAlarmOn);
                    } else {
                        Toast.makeText(WakeupSleepGoal.this, "Please set both sleep and wakeup alarm!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    cancelSleepAlarm();
                    UserService.updateSleepAlarmOn(db,isSleepAlarmOn);
                }

            }
        });


        wakeupAlarmSwitch = findViewById(R.id.wakeup_alarm_on_off);
        wakeupAlarmSwitch.setChecked(wakeupAlarmOn);
        wakeupAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (UserService.getSleepAlarm(db) != "--:--" && UserService.getWakeupAlarm(db) != "--:--") {
                        isWakeupAlarmOn = true;
                        UserService.updateWakeupAlarmOn(db, isWakeupAlarmOn);
                        setWakeupAlarm(wakeupAlarmHour, wakeupAlarmMin);
                    } else {
                        UserService.updateWakeupAlarmOn(db, isWakeupAlarmOn);
                        Toast.makeText(WakeupSleepGoal.this, "Please set both sleep and wakeup alarm!" , Toast.LENGTH_SHORT).show();
                    }
                } else {
                    isWakeupAlarmOn = false;
                    UserService.updateWakeupAlarmOn(db, isWakeupAlarmOn);
                    cancelWakeupAlarm();

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

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(listener, sensor, SensorManager. SENSOR_DELAY_NORMAL);

    }

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
        @Override
        public void onSensorChanged(SensorEvent event) {
            Calendar sleepCheck = Calendar.getInstance();
            sleepCheck.setTimeInMillis(sleepMillis);
            sleepCheck.add(Calendar.MINUTE, 10);

            float value = event.values[0];
            if (sleepCheck.getTimeInMillis() <= System.currentTimeMillis()) {
                if (value < 40) {
                    Log.d("WakeupSleepGoal", "current light" + value);
                    isUserAsleep = true;
                    Log.d("WakeupSleepGoal", "user asleep" + isUserAsleep);
                    if (isUserAsleep) {
                        mSensorManager.unregisterListener(listener);
                        cancelSleepAlarm();
                    }
                } else {
                    Calendar sleepAlarmAgain = Calendar.getInstance();
                    sleepAlarmAgain.setTimeInMillis(sleepMillis);
                    sleepAlarmAgain.add(Calendar.MINUTE, 11);
                    int hour, min;
                    hour = sleepAlarmAgain.get(Calendar.HOUR_OF_DAY);
                    min = sleepAlarmAgain.get(Calendar.MINUTE);
                    setSleepAlarm(hour, min);
                }
            }
        }
    };



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

        loadProfileImg(profile); // load profile pic here in case user gets here through back button
    }

    // load profile img from sdcard, if can't load from assets/
    private void loadProfileImg(ImageView imageView) {
        boolean res = UserService.loadImageForProfile(imageView);
        if (!res) {
            Log.v(TAG, "load Image from storage returns false, try assets/");
            try {
                InputStream inputStream = getAssets().open("user_avatar.jpg");
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                imageView.setImageDrawable(drawable);
                Log.v(TAG, "load from assets.");
            } catch (IOException e) {
                e.printStackTrace();
                Log.v(TAG, "can not load picture from assets");
            }
        }

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
        long millis = calendarSleep.getTimeInMillis();
        sleepMillis = millis;

        alarmManagerSleep = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        pendingIntentSleep = PendingIntent.getBroadcast(this,0,intent,0);
        Log.d(TAG,"sleep milis" + millis);
        Log.d(TAG,"calendar" + calendarSleep.getTime());
        Toast.makeText(WakeupSleepGoal.this, "Sleep Alarm is On. Time: " + calendarSleep.getTime() , Toast.LENGTH_SHORT).show();
        alarmManagerSleep.setExact(AlarmManager.RTC_WAKEUP,calendarSleep.getTimeInMillis(),pendingIntentSleep);
        //everyday
//        alarmManagerSleep.setRepeating(AlarmManager.RTC_WAKEUP,calendarSleep.getTimeInMillis(),alarmManagerSleep.INTERVAL_DAY ,pendingIntentSleep);
    }

    private void setWakeupAlarm(int hour, int min) {
        Intent intent = new Intent(this, AlarmWakeupReceiver.class);

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

        long millis = calendarWakeup.getTimeInMillis();
        wakeupMillis = millis;

        alarmManagerWakeup = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        pendingIntentWakeUp = PendingIntent.getBroadcast(this,1,intent,0);
        Log.d(TAG,"wakeup milis" + millis);
        Log.d(TAG,"calendar" + calendarWakeup.getTime());
        Toast.makeText(WakeupSleepGoal.this, "Wakeup Alarm is On. Time: " + calendarWakeup.getTime() , Toast.LENGTH_SHORT).show();
        alarmManagerWakeup.setExact(AlarmManager.RTC_WAKEUP,calendarWakeup.getTimeInMillis(),pendingIntentWakeUp);
        //everyday
//        alarmManagerWakeup.setRepeating(AlarmManager.RTC_WAKEUP,calendarWakeup.getTimeInMillis(),alarmManagerWakeup.INTERVAL_DAY,pendingIntentWakeUp);
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


}