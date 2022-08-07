package edu.neu.madcourse.wellness_studio.lightExercises;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
<<<<<<< HEAD
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.kofigyan.stateprogressbar.StateProgressBar;

import java.sql.Array;
import java.util.HashMap;
=======
import android.widget.ImageButton;
import android.widget.ImageView;
>>>>>>> b13536133be86f5b2cd3bdbb99ca6475eedfeda5

import edu.neu.madcourse.wellness_studio.MainActivity;
import edu.neu.madcourse.wellness_studio.R;
<<<<<<< HEAD
import edu.neu.madcourse.wellness_studio.utils.Utils;
=======
import edu.neu.madcourse.wellness_studio.profile.Profile;
>>>>>>> b13536133be86f5b2cd3bdbb99ca6475eedfeda5
import localDatabase.enums.ExerciseSet;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import pl.droidsonroids.gif.GifTexImage2D;

public class LightExercises_DuringExercise<pubic> extends AppCompatActivity {
    ExerciseSet focusArea;
    Boolean isChecked;
    int currentSetPosition = 0;
    //temp variable, conect to db
    Boolean stepCompleted1 = false;
    Boolean stepCompleted2 = false;
    Boolean stepCompleted3 = false;
    Boolean stepCompleted4 = false;
    HashMap<Integer, Boolean> stepProgressCompletion = new HashMap<Integer, Boolean>();

    GifDrawable gifDrawable1;
    GifDrawable gifDrawable2;
    GifDrawable gifDrawable3;
    GifDrawable gifDrawable4;
    GifImageView gif1ImageView;
    GifImageView gif2ImageView;
    GifImageView gif3ImageView;
    GifImageView gif4ImageView;
    CheckBox exerciseCompletecheckBox;
    HorizontalScrollView scrollViewForExerciseSets;
    StateProgressBar stateProgressBar;


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

        gif1ImageView = findViewById(R.id.gifImageView1);
        gif2ImageView = findViewById(R.id.gifImageView2);
        gif3ImageView = findViewById(R.id.gifImageView3);
        gif4ImageView = findViewById(R.id.gifImageView4);
        scrollViewForExerciseSets = findViewById(R.id.scrollViewForExerciseSets);
        exerciseCompletecheckBox = findViewById(R.id.exerciseCompletecheckBox);
        stateProgressBar = findViewById(R.id.light_exercises_state_progress_bar_during_exercise);

        loadExerciseSets(focusArea);
        checkBoxOnChangeListener(exerciseCompletecheckBox);
        scrollViewOnChangeListener(scrollViewForExerciseSets);
        Log.d("myApp", "gif1ImageView: " + gif1ImageView);
    }



    //receive focus area intent
    public ExerciseSet receivedAnIntentForChosenFocusedArea() {
        Bundle receiveFocusAreaIntentExtra = getIntent().getExtras();
        if (receiveFocusAreaIntentExtra != null) {
            focusArea = (ExerciseSet) receiveFocusAreaIntentExtra.get("exercises_focus_area");
            Log.d("myApp", "exercises_focus_area: " + focusArea);
        }
        return focusArea;
    }

<<<<<<< HEAD
    //load exercise based on user chosen focus area
    public void loadExerciseSets(ExerciseSet focusArea) {
        if (focusArea == ExerciseSet.ARM) {
            gif1ImageView.setImageResource(R.drawable.arm1);
            gif2ImageView.setImageResource(R.drawable.arm2);
            gif3ImageView.setImageResource(R.drawable.arm3);
            gif4ImageView.setImageResource(R.drawable.arm4);
        }
        if (focusArea == ExerciseSet.BACK) {
            gif1ImageView.setImageResource(R.drawable.back1);
            gif2ImageView.setImageResource(R.drawable.back2);
            gif3ImageView.setImageResource(R.drawable.back3);
            gif4ImageView.setImageResource(R.drawable.back4);
        }
        if (focusArea == ExerciseSet.LEG) {
            gif1ImageView.setImageResource(R.drawable.leg1);
            gif2ImageView.setImageResource(R.drawable.leg2);
            gif3ImageView.setImageResource(R.drawable.leg3);
            gif4ImageView.setImageResource(R.drawable.leg4);
        }
        setLoopCount();
    }

    //set gif animation to play 100 times
    public void setLoopCount() {
        gifDrawable1 = (GifDrawable) gif1ImageView.getDrawable();
        gifDrawable2 = (GifDrawable) gif2ImageView.getDrawable();
        gifDrawable3 = (GifDrawable) gif3ImageView.getDrawable();
        gifDrawable4 = (GifDrawable) gif4ImageView.getDrawable();

        gifDrawable1.setLoopCount(100);
        gifDrawable2.setLoopCount(100);
        gifDrawable3.setLoopCount(100);
        gifDrawable4.setLoopCount(100);
    }

    //click Complete checkbox and set Progress bar
    public void checkBoxOnChangeListener(CheckBox checkbox) {
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    int latestSetPosition = getCurrentSetPosition(scrollViewForExerciseSets.getScrollX(),4,800);
                    if(latestSetPosition != currentSetPosition) {
                        Log.d("myApp", "currentSetPosition: " +  currentSetPosition);
                        currentSetPosition = latestSetPosition;
                    }
                    // perform logic
                    setProgressBarStatus(currentSetPosition);
                    if(stepCompleted1 && stepCompleted2 && stepCompleted3 && stepCompleted4) {
                        stateProgressBar.setAllStatesCompleted(true);
                        Utils.postToast("All sets completed for today!", getApplicationContext());
                        //update state to complete
                    }
                }
            }
        });
    }

    public void scrollViewOnChangeListener(HorizontalScrollView horizontalScrollView) {
        horizontalScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onScrollChanged() {
                 int latestSetPosition = getCurrentSetPosition(horizontalScrollView.getScrollX(),4,800);
                 if(latestSetPosition != currentSetPosition) {
                     boolean latestSetCompletion = getCurrentSetCompletionStatus(latestSetPosition);
                     //if current set is not completed and checkBox is checked, uncheck the checkbox
                     if(!latestSetCompletion &&exerciseCompletecheckBox.isChecked()) {
                         exerciseCompletecheckBox.toggle();
                     }
                     // if current set is completed and checkBox is not checked, check the checkbox
                     if(latestSetCompletion &&!exerciseCompletecheckBox.isChecked()) {
                         exerciseCompletecheckBox.setChecked(true);
                     }
                     //update currentSet to the latest set position
                     currentSetPosition = latestSetPosition;
                 }
            }
        });
    }


    private int getCurrentSetPosition (int scrollX, int totalSets, int widthOfEachPic) {
        int i;
        for(i = 0; i < totalSets; i++) {
            int leftBoundary = i * widthOfEachPic;
            int rightBoundary = (i + 1) * widthOfEachPic;
            if(scrollX >= leftBoundary && scrollX <= rightBoundary) {
                int result = i + 1;
                return result;
            }
        }
        return -1;
    }


    public boolean getCurrentSetCompletionStatus(int currentSetPosition) {
        if(currentSetPosition == 1) {
            return stepCompleted1;
        }
        if(currentSetPosition == 2) {
            return stepCompleted2;
        }
        if(currentSetPosition == 3) {
            return stepCompleted3;
        }
        if(currentSetPosition == 4) {
            return stepCompleted4;
        }
        //if currentSetPosition is zero, return false
        return false;
    }


    public void sendAnIntentForProgressbar(int currentSetPosition) {
        stepProgressCompletion.put(currentSetPosition, true);
        Intent intent = new Intent(this,LightExercises.class);
        intent.putExtra("setsCompletionProgress", stepProgressCompletion);
        startActivity(intent);
    }


    public void setProgressBarStatus(int currentSetPosition) {
        if(currentSetPosition == 1 & stepCompleted1 == false) {
            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
            stepCompleted1 = true;
            Utils.postToast("Set 1 completed!", getApplicationContext());

        }
        if(currentSetPosition == 2 & stepCompleted2 == false) {
            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
            stepCompleted2 = true;
            Utils.postToast("Set 2 completed!", getApplicationContext());

        }
        if(currentSetPosition == 3 & stepCompleted3 == false) {
            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
            stepCompleted3 = true;
            Utils.postToast("Set 3 completed!", getApplicationContext());

        }
        if(currentSetPosition == 4 & stepCompleted4 == false) {
            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
            stepCompleted4 = true;
            Utils.postToast("Set 4 completed!", getApplicationContext());
        }
        sendAnIntentForProgressbar(currentSetPosition);
    }
=======

    // helper to launch activities
    private void goToLightExercise() {
        startActivity(new Intent(LightExercises_DuringExercise.this, LightExercises.class));
    }

    private void goToProfile() {
        startActivity(new Intent(LightExercises_DuringExercise.this, Profile.class));
    }

>>>>>>> b13536133be86f5b2cd3bdbb99ca6475eedfeda5
}