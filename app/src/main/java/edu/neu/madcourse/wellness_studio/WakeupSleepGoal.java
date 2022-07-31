package edu.neu.madcourse.wellness_studio;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Calendar;

import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises_DuringExercise;
import edu.neu.madcourse.wellness_studio.profile.Profile;

public class WakeupSleepGoal extends AppCompatActivity {
    Button sleepAlarmOnOffBtn, wakeupAlarmOnOffBtn;
    TextView sleepAlarmTV, wakeupAlarmTV, sleepHoursTV, sleepAlarmOnTV, wakeupAlarmOnTV;
    ImageView profile, sleepAlarmSetting, wakeupAlarmSetting;
    ImageButton homeBtn, exerciseBtn, sleepBtn, leaderboardBtn;
    String sleepAlarmOnOffCheck = "ALARM OFF", wakeAlarmOnOffCheck = "ALARM OFF";

    ActivityResultLauncher<Intent> startForResult;



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

        startForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result != null && result.getResultCode() == AlarmSetting.RESULT_OK) {
                            if (result.getData() != null &&
                                    result.getData().getStringExtra(AlarmSetting.SLEEP_ALARM_KEY_NAME) != null &&
                                    result.getData().getStringExtra(AlarmSetting.WAKEUP_ALARM_KEY_NAME) != null) {
                                Intent data = result.getData();
                                String sleepAlarmUpdate = data.getStringExtra(AlarmSetting.SLEEP_ALARM_KEY_NAME);
                                String wakeupAlarmUpdate = data.getStringExtra(AlarmSetting.WAKEUP_ALARM_KEY_NAME);
                                Log.d("WakeupSleepGoal", "Sleep = " + sleepAlarmUpdate + "Wakeup = " + wakeupAlarmUpdate );

                                if (!TextUtils.isEmpty(sleepAlarmUpdate) && !TextUtils.isEmpty(wakeupAlarmUpdate))
                                    sleepAlarmTV.setText(sleepAlarmUpdate);
                                    wakeupAlarmTV.setText(wakeupAlarmUpdate);
                                    sleepHoursTV.setText(diff(sleepAlarmUpdate, wakeupAlarmUpdate));
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


        //Home UI buttons
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

    private String diff(String s1, String s2) {

        int time1 = removeColon(s1);
        int time2 = removeColon(s2);

        // difference between hours
        int hourDiff = time2 / 100 - time1 / 100 - 1;

        // difference between minutes
        int minDiff = time2 % 100 + (60 - time1 % 100);

        if (minDiff >= 60) {
            hourDiff++;
            minDiff = minDiff - 60;
        }

        // convert answer again in string with ':'

        String res = String.valueOf(hourDiff) + ':' + String.valueOf(minDiff);
        return res;
    }
    private int removeColon(String s) {
        if (s.length() == 4)
            s = s.substring(0,1) + s.substring(2);

        if (s.length() == 5)
            s = s.substring(0,2) + s.substring(3);

        return Integer.valueOf(s);
    }

}