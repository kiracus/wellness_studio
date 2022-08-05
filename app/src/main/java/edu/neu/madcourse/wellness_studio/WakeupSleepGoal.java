package edu.neu.madcourse.wellness_studio;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.NotificationManager;
import android.app.NotificationChannel;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ToggleButton;

import java.util.Calendar;

import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.AlarmReceiver;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises_DuringExercise;
import edu.neu.madcourse.wellness_studio.profile.Profile;

public class WakeupSleepGoal extends AppCompatActivity {
    TextView sleepAlarmTV, wakeupAlarmTV, sleepHoursTV, sleepAlarmOnTV, wakeupAlarmOnTV;
    ImageView profile, sleepAlarmSetting, wakeupAlarmSetting;
    ImageButton homeBtn, exerciseBtn, sleepBtn, leaderboardBtn;
    String sleepAlarmReopenUpdate, wakeupAlarmReopenUpdate, sleepHoursReopenUpdate;
    ActivityResultLauncher<Intent> startForResult;
    Switch sleepAlarmSwitch, wakeupAlarmSwitch;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    String sleepAlarmUpdate, wakeupAlarmUpdate;
    int sleepAlarmHour = 22, sleepAlarmMin = 30, wakeupAlarmHour = 8, wakeupAlarmMin = 30;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup_sleep_goal);

        profile = findViewById(R.id.profile_image);

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

        sleepAlarmOnTV = findViewById(R.id.alarm_on_TV);
        wakeupAlarmOnTV = findViewById(R.id.wakeup_alarm_on_TV);

        startForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result != null && result.getResultCode() == AlarmSetting.RESULT_OK) {
                            if (result.getData() != null &&
                                    result.getData().getStringExtra(AlarmSetting.SLEEP_ALARM_KEY_NAME) != null &&
                                    result.getData().getStringExtra(AlarmSetting.WAKEUP_ALARM_KEY_NAME) != null){
                                Intent data = result.getData();
                                sleepAlarmUpdate = data.getStringExtra(AlarmSetting.SLEEP_ALARM_KEY_NAME);

                                wakeupAlarmUpdate = data.getStringExtra(AlarmSetting.WAKEUP_ALARM_KEY_NAME);
                                Log.d("WakeupSleepGoal", "Sleep = " + sleepAlarmUpdate + "Wakeup = " + wakeupAlarmUpdate );

                                if (!TextUtils.isEmpty(sleepAlarmUpdate) && !TextUtils.isEmpty(wakeupAlarmUpdate))
                                    sleepAlarmTV.setText(sleepAlarmUpdate);
                                    sleepAlarmReopenUpdate = sleepAlarmUpdate;
                                    Log.d("WakeupSleepGoal", "sleepAlarmUpdate" + sleepAlarmReopenUpdate);
                                    sleepAlarmHour = getHour(sleepAlarmUpdate);
                                    sleepAlarmMin = getMin(sleepAlarmUpdate);
                                    wakeupAlarmTV.setText(wakeupAlarmUpdate);
                                    wakeupAlarmReopenUpdate = wakeupAlarmUpdate;
                                    wakeupAlarmHour = getHour(wakeupAlarmUpdate);
                                    wakeupAlarmMin = getMin(wakeupAlarmUpdate);
                                Log.d("WakeupSleepGoal", "wakeupAlarmUpdate" + wakeupAlarmReopenUpdate);
                                    sleepHoursTV.setText(calculateDiffTime(sleepAlarmUpdate, wakeupAlarmUpdate));
                                    sleepHoursReopenUpdate = calculateDiffTime(sleepAlarmUpdate, wakeupAlarmUpdate);
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
                    setAlarm(sleepAlarmHour, sleepAlarmMin);
                    sleepAlarmOnTV.setText("ALARM ON");
                    Toast.makeText(WakeupSleepGoal.this, "Sleep Alarm is On.", Toast.LENGTH_SHORT).show();
                } else {
                    cancelSleepAlarm();
                    Toast.makeText(WakeupSleepGoal.this, "Sleep Alarm is Off.", Toast.LENGTH_SHORT).show();
                    sleepAlarmOnTV.setText("ALARM OFF");
                }
            }
        });
        wakeupAlarmSwitch = findViewById(R.id.wakeup_alarm_on_off);

        wakeupAlarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setAlarm(wakeupAlarmHour, wakeupAlarmMin);
                    Toast.makeText(WakeupSleepGoal.this, "Wakeup Alarm is On.", Toast.LENGTH_SHORT).show();
                    wakeupAlarmOnTV.setText("ALARM ON");
                } else {
                    cancelSleepAlarm();
                    Toast.makeText(WakeupSleepGoal.this, "Wakeup Alarm is Off.", Toast.LENGTH_SHORT).show();
                    wakeupAlarmOnTV.setText("ALARM OFF");
                }
            }
        });

        //Home UI buttons
        homeBtn = findViewById(R.id.imageButton_home);
        exerciseBtn = findViewById(R.id.imageButton_exercise);
        sleepBtn = findViewById(R.id.imageButton_sleep);
        leaderboardBtn = findViewById(R.id.imageButton_leaderboard);
        homeBtn.setOnClickListener(v -> startActivity(new Intent(WakeupSleepGoal.this, Greeting.class)));

        // set click listeners for buttons
        exerciseBtn.setOnClickListener(v -> goToLightExercise());
        //exerciseGoBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LightExercises.class)));
//        sleepBtn.setOnClickListener(v -> startActivity(new Intent(WakeupSleepGoal.this, WakeupSleepGoal.class)));
        sleepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WakeupSleepGoal.this, WakeupSleepGoal.class));
//                updateAlarmReopen(v);
            }
        });
        leaderboardBtn.setOnClickListener(v -> startActivity(new Intent(WakeupSleepGoal.this, Leaderboard.class)));



        
        //notification 
        createNotificationChannel();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "alarmAndroidChannel";
            String description = "channel for alarm manager";
            int important = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel1 = new NotificationChannel("alarmAndroid", name, important);
            channel1.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
        }
    }

    private void goToLightExercise() {
        startActivity(new Intent(WakeupSleepGoal.this, LightExercises.class));
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

    public void setAlarm(int hour, int min) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        long millis = convertHourAndMinToMilliSeconds(hour, min);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        Log.d("myApp","milis" + millis);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,millis,AlarmManager.INTERVAL_DAY,pendingIntent);
        Toast.makeText(getApplicationContext(),"Alarm is on",Toast.LENGTH_SHORT).show();
    }

    private long convertHourAndMinToMilliSeconds(int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE,min);
        Log.d("myApp","calenar" + calendar.getTime());
        long millis = calendar.getTimeInMillis();
        return millis;
    }
    public void cancelSleepAlarm() {
        Intent intent = new Intent(this,AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        if(alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        alarmManager.cancel(pendingIntent);
        Toast.makeText(getApplicationContext(),"Alarm is off",Toast.LENGTH_SHORT).show();
    }
}