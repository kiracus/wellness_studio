package edu.neu.madcourse.wellness_studio;

import android.app.AlarmManager;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmSetting extends AppCompatActivity {
    NumberPicker sleepHourNumberPicker, sleepMinNumberPicker, wakeupHourNumberPicker, wakeupMinNumberPicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_alarm_time);

        sleepHourNumberPicker = (NumberPicker)findViewById(R.id.hour_picker);
        sleepMinNumberPicker = (NumberPicker)findViewById(R.id.minute_picker);
        wakeupHourNumberPicker = (NumberPicker)findViewById(R.id.wakeup_hour_picker);
        wakeupMinNumberPicker = (NumberPicker)findViewById(R.id.wakeup_minute_picker);

        sleepHourNumberPicker.setMaxValue(23);
        sleepHourNumberPicker.setMinValue(0);
        sleepMinNumberPicker.setMaxValue(59);
        sleepMinNumberPicker.setMinValue(0);

        wakeupHourNumberPicker.setMaxValue(23);
        wakeupHourNumberPicker.setMinValue(0);
        wakeupMinNumberPicker.setMaxValue(59);
        wakeupMinNumberPicker.setMinValue(0);



//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, );
    }


}
