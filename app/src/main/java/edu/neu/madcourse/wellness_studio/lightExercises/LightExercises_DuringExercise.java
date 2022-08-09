package edu.neu.madcourse.wellness_studio.lightExercises;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.util.Date;
import java.util.HashMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import edu.neu.madcourse.wellness_studio.MainActivity;
import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import edu.neu.madcourse.wellness_studio.utils.Utils;
import edu.neu.madcourse.wellness_studio.profile.Profile;
import localDatabase.AppDatabase;
import localDatabase.enums.ExerciseSet;
import localDatabase.enums.ExerciseStatus;
import localDatabase.lightExercise.LightExercise;
import localDatabase.userInfo.User;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import pl.droidsonroids.gif.GifTexImage2D;

public class LightExercises_DuringExercise<pubic> extends AppCompatActivity {
    private String TAG = "LightExercises_DuringExercise";
    AppDatabase db;
    LightExercise lightExercise;

    ExerciseSet focusArea;
    Boolean isChecked;
    int currentSetPosition = 0;
    //temp variable, conect to db
    Boolean stepCompleted1;
    Boolean stepCompleted2;
    Boolean stepCompleted3;
    Boolean stepCompleted4;
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

        db = AppDatabase.getDbInstance(this.getApplicationContext());

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

        //set listeners for changes
        checkBoxOnChangeListener(exerciseCompletecheckBox);
        scrollViewOnChangeListener(scrollViewForExerciseSets);

        profileIV.setOnClickListener( v-> goToProfile());
        loadProfileImg(profileIV);

        //connect to db and load previous data
        lightExercise = UserService.getCurrentLightExercise(db);
        ExerciseStatus currentExerciseStatus = lightExercise.getExerciseStatus();
        Log.d(TAG,"get current currentExerciseStatus from db: " + currentExerciseStatus);

        stepCompleted1 = lightExercise.getStepOneCompleted();
        stepCompleted2 = lightExercise.getStepTwoCompleted();
        stepCompleted3 = lightExercise.getStepThreeCompleted();
        stepCompleted4 = lightExercise.getStepFourCompleted();
        Log.d(TAG,"get current stepCompleted1 from db: " + stepCompleted1);
        Log.d(TAG,"get current stepCompleted2 from db: " + stepCompleted2);
        Log.d(TAG,"get current stepCompleted3 from db: " + stepCompleted3);
        Log.d(TAG,"get current stepCompleted4 from db: " + stepCompleted4);

        if(lightExercise != null && !currentExerciseStatus.equals(ExerciseStatus.NOT_STARTED)) {
            if(lightExercise.getCurrentStep() != null) {
                currentSetPosition = Integer.parseInt(lightExercise.getCurrentStep());
                Log.d(TAG,"get current step from db: " + currentSetPosition);
                //load progress bar to the most recent completed set from last time
                setProgressBarStatus(currentSetPosition,false);
                //automatically scroll to position at where was last most recent set
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setCurrentSetPosition(currentSetPosition, 800);
                    }
                },100);

                //setCompleteButton on the last completed position when it got scrolled to the most recent completed set
                if(getCurrentSetCompletionStatus(currentSetPosition)) {
                    exerciseCompletecheckBox.setChecked(true);
                }
            }
        }
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
                    // perform logic and save changes to db
                    setCurrentSetCompletionStatus(currentSetPosition);
                    setProgressBarStatus(currentSetPosition,true);
                    UserService.updateCurrentStep(db,currentSetPosition);
                    UserService.updateStepCompletion(db,currentSetPosition,true);
                    updateExerciseStatus(currentSetPosition);
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
                 Log.d(TAG,"scrollChangePosition: " + latestSetPosition);
                 if(latestSetPosition != currentSetPosition && latestSetPosition != -1) {
                     boolean latestSetCompletion = getCurrentSetCompletionStatus(latestSetPosition);
                     //if current set is not completed and checkBox is checked, uncheck the checkbox
                     if(!latestSetCompletion && exerciseCompletecheckBox.isChecked()) {
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


    private int getCurrentSetPosition(int scrollX, int totalSets, int widthOfEachPic) {
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

    public void setCurrentSetPosition(int currentCompletedSetPosition, int widthOfEachPic) {
        if(currentCompletedSetPosition == 1) {
            scrollViewForExerciseSets.scrollTo(0, 0);
            Log.d(TAG, "setCurrentSetPosition scroll to: " + 1);
        }
        if(currentCompletedSetPosition == 2) {
            scrollViewForExerciseSets.scrollTo(widthOfEachPic * 1, 0);
            Log.d(TAG, "setCurrentSetPosition scroll to: " + 2);
        }
        if(currentCompletedSetPosition == 3) {
            scrollViewForExerciseSets.scrollTo(widthOfEachPic * 2, 0);
            Log.d(TAG, "setCurrentSetPosition scroll to: " + 3);
        }
        if(currentCompletedSetPosition == 4) {
            scrollViewForExerciseSets.scrollTo(widthOfEachPic * 3,0);
            Log.d(TAG, "setCurrentSetPosition scroll to: " + 4);
        }
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

    public void setCurrentSetCompletionStatus(int currentSetPosition) {
        if(currentSetPosition == 1) {
             stepCompleted1 = true;
        }
        if(currentSetPosition == 2) {
            stepCompleted2 = true;
        }
        if(currentSetPosition == 3) {
            stepCompleted3 = true;
        }
        if(currentSetPosition == 4) {
            stepCompleted4 = true;
        }
    }

    //set progress bar, and set if toast will be posted
    public void setProgressBarStatus(int currentSetPosition, boolean postToast) {
        if(stepCompleted1) {
            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
            if(postToast) {
                Utils.postToast("Set 1 completed!", getApplicationContext());
            }

        }
        if(stepCompleted2) {
            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
            if(postToast) {
                Utils.postToast("Set 2 completed!", getApplicationContext());
            }
        }
        if(stepCompleted3) {
            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
            if(postToast) {
                Utils.postToast("Set 3 completed!", getApplicationContext());
            }
        }
        if(stepCompleted4) {
            stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
            if(postToast) {
                Utils.postToast("Set 4 completed!", getApplicationContext());
            }
        }
        if(stepCompleted1 && stepCompleted2 && stepCompleted3 && stepCompleted4) {
            stateProgressBar.setAllStatesCompleted(true);
            if(postToast) {
                Utils.postToast("All sets completed for today!", getApplicationContext());
            }
        }
    }

    // helper to launch activities
    private void goToLightExercise() {
        startActivity(new Intent(LightExercises_DuringExercise.this, LightExercises.class));
    }

    private void goToProfile() {
        startActivity(new Intent(LightExercises_DuringExercise.this, Profile.class));
    }

    // load profile img from sdcard, if can't load from assets/
    private void loadProfileImg(ImageView imageView) {
        boolean res = UserService.loadImageForProfile(imageView);
        if (!res) {
            Log.v(TAG, "load Image from storage returns false, try assets/");
            try {
                InputStream inputStream = getAssets().open("user_avatar.jpg");
                Drawable drawable = Drawable.createFromStream(inputStream, null);
                imageView.setImageDrawable(drawable);
                Log.v(TAG, "load from assets.");
            } catch (IOException e) {
                e.printStackTrace();
                Log.v(TAG, "can not load picture from assets");
            }
        }
    }

    public void updateExerciseStatus(int currentSetPosition) {
        if(currentSetPosition == 0) {
            UserService.updateExerciseStatus(db,ExerciseStatus.NOT_STARTED, Utils.getCurrentDate());
            Log.d(TAG,"updateExerciseStatus" + ExerciseStatus.NOT_STARTED);
        }
        if(currentSetPosition <= 4 && !stepCompleted4) {
            UserService.updateExerciseStatus(db,ExerciseStatus.NOT_FINISHED, Utils.getCurrentDate());
            Log.d(TAG,"updateExerciseStatus" + ExerciseStatus.NOT_FINISHED);
        }
        if(currentSetPosition == 4 && stepCompleted1 && stepCompleted2 && stepCompleted3 && stepCompleted4) {
            UserService.updateExerciseStatus(db,ExerciseStatus.COMPLETED, Utils.getCurrentDate());
            UserService.updateExerciseGoalStatus(db,true,Utils.getCurrentDate());
            Log.d(TAG,"updateExerciseStatus" + ExerciseStatus.COMPLETED);
        }
        Log.d(TAG,"updateExerciseStatus: " + currentSetPosition);

    }
}