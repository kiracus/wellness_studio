package edu.neu.madcourse.wellness_studio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.sql.Date;
import java.util.List;

import localDatabase.AppDatabase;
import localDatabase.Enums.ExerciseStatus;
import localDatabase.LightExercise;

public class LightExercises extends AppCompatActivity {
    AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_exercises);

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