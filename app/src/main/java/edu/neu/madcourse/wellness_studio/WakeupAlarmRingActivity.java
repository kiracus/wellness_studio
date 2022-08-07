package edu.neu.madcourse.wellness_studio;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

public class WakeupAlarmRingActivity extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup_ring);

        Button dismiss = findViewById(R.id.dismiss_alarm_button);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                getApplicationContext().stopService(intentService);
                finish();

            }
        });

        Button snooze = findViewById(R.id.snooze_alarm_button);
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int wakeupAlarmHour, wakeupAlarmMin;
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.MINUTE, 5);

                AlarmManager alarmManagerWakeup = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), AlarmWakeupReceiver.class);
                PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(WakeupAlarmRingActivity.this, 1, intent, 0);

                wakeupAlarmHour = calendar.get(Calendar.HOUR_OF_DAY);
                wakeupAlarmMin = calendar.get(Calendar.MINUTE);
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, wakeupAlarmHour);
                calendar.set(Calendar.MINUTE, wakeupAlarmMin);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);


                long millis = calendar.getTimeInMillis();

                Log.d("WakeupAlarmRingActivity", "snooze hour" + wakeupAlarmHour + "snooze min" + wakeupAlarmMin);

                alarmManagerWakeup.setExact(AlarmManager.RTC_WAKEUP, millis, alarmPendingIntent);

                Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                getApplicationContext().stopService(intentService);
                finish();
            }

        });
    }

}
