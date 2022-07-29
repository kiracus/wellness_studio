package edu.neu.madcourse.wellness_studio.lightExercises;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import edu.neu.madcourse.wellness_studio.R;
import localDatabase.enums.ExerciseSet;

public class LightExercises_DuringExercise<pubic> extends AppCompatActivity {
    ExerciseSet focusArea;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_exercises_during_exercise);
        focusArea = receivedAnIntentForChosenFocusedArea();
    }

    public ExerciseSet receivedAnIntentForChosenFocusedArea() {
        Bundle receiveFocusAreaIntentExtra = getIntent().getExtras();
        if (receiveFocusAreaIntentExtra != null) {
            focusArea = (ExerciseSet) receiveFocusAreaIntentExtra.get("exercises_focus_area");
            Log.d("myApp", "exercises_focus_area: " + focusArea);
        }
        return focusArea;
    }
}