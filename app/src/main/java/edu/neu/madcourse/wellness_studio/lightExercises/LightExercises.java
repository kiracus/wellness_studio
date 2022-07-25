package edu.neu.madcourse.wellness_studio.lightExercises;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
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
import java.util.List;
import java.util.Locale;

import edu.neu.madcourse.wellness_studio.R;
import localDatabase.AppDatabase;
import localDatabase.enums.ExerciseStatus;
import localDatabase.lightExercise.LightExercise;

public class LightExercises extends AppCompatActivity {
    AppDatabase appDatabase;
    int hour, min;
    TextView timeTextView;
    Switch reminderSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_exercises);

        View view = findViewById(R.id.activity_light_exercises);
        timeTextView = findViewById(R.id.timeTextView);
        reminderSwitch = findViewById(R.id.reminderSwitch);

        popTimePicker(view);
        setReminderSwitch(reminderSwitch);

        appDatabase = AppDatabase.getDbInstance(this.getApplicationContext());

        LightExercise lightExercise = getCurrentLightExercise();
        if (lightExercise == null) {
            long mili = System.currentTimeMillis();
            Date date = new java.sql.Date(mili);
            lightExercise = new LightExercise();
            lightExercise.setDate(date);
            createNewLightExercise(lightExercise);
        }
        else {
            lightExercise.setExerciseStatus(ExerciseStatus.NOT_STARTED);
            updateLightExerciseInfo(lightExercise);
        }
        loadLightExerciseInfo();
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

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,style,onTimeSetListener,hour,min,false);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    //set reminder switch
    public void setReminderSwitch(Switch reminderSwitch) {
        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(getApplicationContext(),"reminder is on",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"reminder is off",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void createNewLightExercise(LightExercise lightExercise) {
        appDatabase.lightExerciseDao().insertLightExercise(lightExercise);
    }

    public void updateLightExerciseInfo(LightExercise lightExercise) {
        AppDatabase appDatabase = AppDatabase.getDbInstance(this.getApplicationContext());
        appDatabase.lightExerciseDao().updateLightExercise(lightExercise);
    }

    public void loadLightExerciseInfo() {
        LightExercise lightExercise = getCurrentLightExercise();
        Date date = lightExercise.getDate();
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