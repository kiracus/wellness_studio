package edu.neu.madcourse.wellness_studio.lightExercises;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import edu.neu.madcourse.wellness_studio.R;

public class LightExercises_DuringExercise extends AppCompatActivity {
    String focusArea;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_exercises_during_exercise);

        Bundle receiveFocusAreaIntentExtra = getIntent().getExtras();
        if(receiveFocusAreaIntentExtra != null) {
            focusArea = receiveFocusAreaIntentExtra.getString("exercises_focus_area");
            Log.d("myApp","exercises_focus_area: " + focusArea);
        }
    }
}