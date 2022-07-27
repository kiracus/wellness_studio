package edu.neu.madcourse.wellness_studio.lightExercises;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import edu.neu.madcourse.wellness_studio.R;
import localDatabase.AppDatabase;
import localDatabase.enums.ExerciseStatus;
import localDatabase.lightExercise.LightExercise;

public class LightExercises extends AppCompatActivity {
    AppDatabase appDatabase;
    int hour, min = -1;
    TextView timeTextView;
    Switch reminderSwitch;

    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_exercises);
        createNotificationChannel();

        timeTextView = findViewById(R.id.timeTextView);
        reminderSwitch = findViewById(R.id.reminderSwitch);

        setReminderSwitch(reminderSwitch);

        appDatabase = AppDatabase.getDbInstance(this.getApplicationContext());

        LightExercise lightExercise = getCurrentLightExercise();
        if (lightExercise == null) {
            long mili = System.currentTimeMillis();
            Date date = new java.sql.Date(mili);
            lightExercise = new LightExercise();
//            lightExercise.setDate(date);
            createNewLightExercise(lightExercise);
        }
        else {
            lightExercise.setExerciseStatus(ExerciseStatus.NOT_STARTED);
            updateLightExerciseInfo(lightExercise);
        }
        loadLightExerciseInfo();
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "lightExerciseAndroidChannel";
            String description = "Channel for light exercise alarm";
            int important = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("lightExerciseAndroid",name,important);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


    //timePicker
    public void popTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                min = minute;
                String time  = String.format(Locale.getDefault(),"%02d:%02d",hour,min);

                String reminder = "Reminder" + "\n" + time;
                SpannableString spannableString = new SpannableString(reminder);
                spannableString.setSpan(new RelativeSizeSpan(2f),0,8,0);
                spannableString.setSpan(new ForegroundColorSpan(Color.BLACK),0,8,0);
                timeTextView.setText(spannableString);
            }
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,style,onTimeSetListener,hour,min,true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    //set reminder switch
    public void setReminderSwitch(Switch reminderSwitch) {
        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    //when alarm hasn't been set and user turns the switch to ON
                    if(hour == -1) {
                        Toast.makeText(getApplicationContext(), "please set an alarm", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    setAlarm();
                }
                else {
                    cancelAlarm();
                }
            }
        });
    }

    // turns on the alarm, a slight couple mins delay between setted time and actual notification time is expected.
    public void setAlarm() {
        Intent intent = new Intent(this,AlarmReceiver.class);
        long millis = convertHourAndMinToMilliSeconds();

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        Log.d("myApp","milis" + millis);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,millis,AlarmManager.INTERVAL_DAY,pendingIntent);
        Toast.makeText(getApplicationContext(),"reminder is on",Toast.LENGTH_SHORT).show();
    }

    //turns off the alarm
    public void cancelAlarm() {
        Intent intent = new Intent(this,AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        if(alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        alarmManager.cancel(pendingIntent);
        Toast.makeText(getApplicationContext(),"reminder is off",Toast.LENGTH_SHORT).show();
    }

    private long convertHourAndMinToMilliSeconds() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,hour,min);
        Log.d("myApp","calenar" + calendar.getTime());
        long millis = calendar.getTimeInMillis();
        return millis;
    }

    //database
    public void createNewLightExercise(LightExercise lightExercise) {
        appDatabase.lightExerciseDao().insertLightExercise(lightExercise);
    }

    public void updateLightExerciseInfo(LightExercise lightExercise) {
        AppDatabase appDatabase = AppDatabase.getDbInstance(this.getApplicationContext());
        appDatabase.lightExerciseDao().updateLightExercise(lightExercise);
    }

    public void loadLightExerciseInfo() {
        LightExercise lightExercise = getCurrentLightExercise();
        String date = lightExercise.getDate();
        ExerciseStatus exerciseStatus = lightExercise.getExerciseStatus();
        Log.d("Myapp","date: " + date + "exerciseStatus: " + exerciseStatus);
    }

    private LightExercise getCurrentLightExercise() {
        List<LightExercise> LightExerciseList = appDatabase.lightExerciseDao().getLightExercise();
        if(LightExerciseList.size() != 0) {
            return LightExerciseList.get(0);
        }
        else
            return null;
    }
}