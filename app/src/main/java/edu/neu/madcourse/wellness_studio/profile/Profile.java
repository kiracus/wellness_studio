package edu.neu.madcourse.wellness_studio.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.neu.madcourse.wellness_studio.Greeting;
import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.WakeupSleepGoal;
import edu.neu.madcourse.wellness_studio.customCalendar.CustomCalendar;
import edu.neu.madcourse.wellness_studio.customCalendar.OnDateSelectedListener;
import edu.neu.madcourse.wellness_studio.customCalendar.OnNavigationButtonClickedListener;
import edu.neu.madcourse.wellness_studio.customCalendar.Property;
import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import edu.neu.madcourse.wellness_studio.utils.Utils;
import localDatabase.AppDatabase;
import localDatabase.userInfo.User;

public class Profile extends AppCompatActivity implements OnNavigationButtonClickedListener {
    // test
    protected static String TAG = "profile";

    protected final static String DEFAULT = "default";
    protected final static String SELECTED = "selected";
    protected final static String CHECKED = "checked";
    protected final static String CURRENT = "current";

    // VI
    ImageButton settingBtn, loginBtn;
    ImageView profileImgIV;
    TextView nicknameTV;
    CheckBox goalFinishedCB;
    ImageButton homeBtn, exerciseBtn, sleepBtn, leaderboardBtn;
    CustomCalendar customCalendar;

    // db
    protected AppDatabase db;

    // firebase auth
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;

    // calendar
    Calendar calendar;
    protected static int MONTH = 0;
    protected static HashMap<Object, Property> propertyMap;
    protected static HashMap<Integer, Object> dateMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // initialize db instance
        db = AppDatabase.getDbInstance(this.getApplicationContext());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        calendar = Calendar.getInstance();
        MONTH = calendar.get(Calendar.MONTH);
        Log.v(TAG, "current month: " + (MONTH+1));

        // get VI components
        customCalendar = findViewById(R.id.custom_calendar);

        settingBtn = findViewById(R.id.imageButton_setting);
        loginBtn = findViewById(R.id.imageButton_login);

        profileImgIV = findViewById(R.id.profile_img);
        nicknameTV = findViewById(R.id.nickname_profile);
        goalFinishedCB = findViewById(R.id.goal_finished_checker);

        homeBtn = findViewById(R.id.imageButton_home);
        exerciseBtn = findViewById(R.id.imageButton_exercise);
        sleepBtn = findViewById(R.id.imageButton_sleep);
        leaderboardBtn = findViewById(R.id.imageButton_leaderboard);

        // show username and profile img TODO: img
        nicknameTV.setText(UserService.getNickname(db));

        // set buttons
        homeBtn.setOnClickListener(v -> startActivity(new Intent(Profile.this, Greeting.class)));
        exerciseBtn.setOnClickListener(v -> goToLightExercise());
        sleepBtn.setOnClickListener(v -> startActivity(new Intent(Profile.this, WakeupSleepGoal.class)));
        leaderboardBtn.setOnClickListener(v -> startActivity(new Intent(Profile.this, Leaderboard.class)));
        settingBtn.setOnClickListener(v -> startActivity(new Intent(Profile.this, ChangeProfile.class)));


        // set calendar
        // initialize hashmap to hold properties,
        // key is a string of description, value is a property obj
        propertyMap = new HashMap<>();
        setPropertyMap();
        customCalendar.setPropertyMap(propertyMap);

        // initialize hashmap holding dates with description and properties
        // map day (int) to str desc of property
        dateMap = new HashMap<>();
        dateMap.put(calendar.get(Calendar.DAY_OF_MONTH), CURRENT);  // today is the current date

        // get a list of dates with finished goal status
        String currdate = Utils.getCurrentDate();
        Log.v(TAG, "checking dates in month : " + currdate.substring(0, 7));
        List<String> checkedDates = UserService.getFinishedDatesOfMonth(db, currdate.substring(0, 7));
        Log.v(TAG, checkedDates.toString());

        // mark checked date as property "CHECKED" (green)
        if (checkedDates != null) {
            Log.v(TAG, checkedDates.toString());
            for (String date : checkedDates) {
                Integer d = Integer.parseInt(date.substring(8));
                dateMap.put(d, CHECKED);
            }
        }

        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this);

        customCalendar.setDate(calendar, dateMap);

        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {
                int date = selectedDate.get(Calendar.DAY_OF_MONTH);
                dateMap.put(date, SELECTED);

                Snackbar.make(customCalendar, selectedDate.get(Calendar.DAY_OF_MONTH)
                        + " / " + (selectedDate.get(Calendar.MONTH) + 1)
                        + " / " + selectedDate.get(Calendar.YEAR)
                        + " selected", Snackbar.LENGTH_LONG).show();
            }
        });

        // TODO set goal checker
    }

    // check user online status, responsible for ser online related VI components
    @Override
    public void onStart() {
        super.onStart();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Check if user is signed in (non-null) and update UI accordingly.
                fUser = mAuth.getCurrentUser();
                if(fUser != null){  // signed in,
                    loginBtn.setImageResource(R.drawable.ic_baseline_logout_24);
                }
            }
        });
    }

    // return new datemap when go to prev / next month
    @Override
    public Map<Integer, Object>[] onNavigationButtonClicked(int whichButton, Calendar newMonth) {
        Calendar calendar = Calendar.getInstance();
//        Log.v(TAG, "new year: " + newMonth.get(Calendar.YEAR));
//        Log.v(TAG, "new month: " + (newMonth.get(Calendar.MONTH)+1));
        String year = newMonth.get(Calendar.YEAR)+"";
        int monthInt = newMonth.get(Calendar.MONTH)+1;
        String month = monthInt<10 ? "0"+monthInt : ""+monthInt;

        String newYearMonth = year + "-" + month;
        Log.v(TAG, "new year and month: " + newYearMonth);

        Map<Integer, Object>[] arr = new Map[2];
        arr[0] = new HashMap<>();
        List<String> finishedDates = UserService.getFinishedDatesOfMonth(db, newYearMonth);

        // mark checked date as property "CHECKED" (green)
        if (finishedDates != null) {
            for (String date : finishedDates) {
                Integer d = Integer.parseInt(date.substring(8));
                arr[0].put(d, CHECKED);
            }
        }
        // if back to curr month, mark today as current
        if (newYearMonth.equals(Utils.getCurrentDate().substring(0,7))) {
            arr[0].put(calendar.get(Calendar.DAY_OF_MONTH), CURRENT);
        }
        arr[1] = null;  //Optional: keep this for the map linking a date to its tag.
        return arr;
    }

    // save different properties into the property map
    private void setPropertyMap() {
        if (propertyMap == null) {
            return;
        }
        // default property (no bg color, only date)
        Property propDefault = new Property();
        propDefault.layoutResource = R.layout.default_view;
        propDefault.dateTextViewResource = R.id.default_text;
        propertyMap.put("default", propDefault);

        // initialize current date
        Property propCurrent = new Property();
        propCurrent.layoutResource = R.layout.current_view;
        propCurrent.dateTextViewResource = R.id.current_text;
        propertyMap.put("current", propCurrent);

        // checked date (when exercise goal finished)
        Property propChecked = new Property();
        propChecked.layoutResource = R.layout.checked_view;
        propChecked.dateTextViewResource = R.id.checked_text;
        propertyMap.put("checked", propChecked);

        // selected date (when exercise goal finished)
        Property propSelected = new Property();
        propSelected.layoutResource = R.layout.selected_view;
        propSelected.dateTextViewResource = R.id.selected_text;
        propertyMap.put("selected", propSelected);
    }

    private void goToLightExercise() {
        startActivity(new Intent(Profile.this, LightExercises.class));
    }
}