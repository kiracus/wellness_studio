package edu.neu.madcourse.wellness_studio.lightExercises;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import edu.neu.madcourse.wellness_studio.MainActivity;
import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.profile.Profile;
import localDatabase.enums.ExerciseSet;

public class LightExercises_DuringExercise<pubic> extends AppCompatActivity {
    ExerciseSet focusArea;
    ImageView set1ImageView;
    ImageView set2ImageView;
    ImageView set3ImageView;
    ImageView set4ImageView;

    ImageButton backBtn;
    ImageView profileIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_exercises_during_exercise);

        // VI components - buttons
        backBtn = findViewById(R.id.imageButton_back);
        profileIV = findViewById(R.id.imageView_profile);

        // set buttons
        backBtn.setOnClickListener(v -> goToLightExercise());
        profileIV.setOnClickListener(v -> goToProfile());

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


    // helper to launch activities
    private void goToLightExercise() {
        startActivity(new Intent(LightExercises_DuringExercise.this, LightExercises.class));
    }

    private void goToProfile() {
        startActivity(new Intent(LightExercises_DuringExercise.this, Profile.class));
    }

}