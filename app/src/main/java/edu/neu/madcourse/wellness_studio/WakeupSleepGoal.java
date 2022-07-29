package edu.neu.madcourse.wellness_studio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class WakeupSleepGoal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button alarmSettingBtn, sleepAlarmOnOffBtn, wakeupAlarmOnOffBtn;
        TextView sleepAlarmTV, wakeupAlarmTV, sleepHoursTV;
        ImageView profile, sleepAlarmSetting, wakeupAlarmSetting;
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

        sleepAlarmSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingActivity();
            }
        });

        wakeupAlarmSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingActivity();
            }
        });





    }

    private void openSettingActivity() {
        startActivity(new Intent(WakeupSleepGoal.this, AlarmSetting.class));
    }
}