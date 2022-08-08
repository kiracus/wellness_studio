package edu.neu.madcourse.wellness_studio;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class WakeupAlarmRingActivity extends AppCompatActivity {
    Context context;
    ProgressBar shakeProgressBar;
    TextView shakeProgressTV;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double accelerationCurrentValue;
    private double accelerationPreviousValue;
    int shakeTotalCount;
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        public int countShake = 0;
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            accelerationCurrentValue = Math.sqrt((x * x + y * y + z * z));
            double changeInAcceleration = Math.abs(accelerationCurrentValue - accelerationPreviousValue);
            accelerationPreviousValue = accelerationCurrentValue;
            shakeProgressTV.setText("Shake Count: " + shakeTotalCount);
            if (changeInAcceleration > 2) {
                countShake++;
                shakeTotalCount++;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wakeup_ring);

        Button dismiss = findViewById(R.id.dismiss_alarm_button);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WakeupSleepGoal.isWakeupSensorUse.equals("OFF")) {
                    Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                    getApplicationContext().stopService(intentService);
                    finish();
                } else {
                    //shake sensor
                    if (shakeTotalCount >= 20) {
                        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
                        getApplicationContext().stopService(intentService);
                        finish();
                    } else {
                        Toast.makeText(WakeupAlarmRingActivity.this, "please shake your phone at least 20 times to dismiss alarm.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        Button snooze = findViewById(R.id.snooze_alarm_button);
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WakeupSleepGoal.isSnooze.equals("OFF") ) {
                    Toast.makeText(WakeupAlarmRingActivity.this, "Sorry, you cannot snooze alarm this time, please turn on the snooze next time.", Toast.LENGTH_SHORT).show();
                    return;
                }

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
                Toast.makeText(WakeupAlarmRingActivity.this, "You choose snooze, will alarm again in 5 mins.", Toast.LENGTH_SHORT).show();
                finish();
            }

        });

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        shakeProgressTV = findViewById(R.id.shake_progress_TV);

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

}
