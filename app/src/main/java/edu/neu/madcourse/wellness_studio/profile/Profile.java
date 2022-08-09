package edu.neu.madcourse.wellness_studio.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.neu.madcourse.wellness_studio.Greeting;
import edu.neu.madcourse.wellness_studio.MainActivity;
import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.WakeupSleepGoal;
import edu.neu.madcourse.wellness_studio.customCalendar.CustomCalendar;
import edu.neu.madcourse.wellness_studio.customCalendar.OnDateSelectedListener;
import edu.neu.madcourse.wellness_studio.customCalendar.OnNavigationButtonClickedListener;
import edu.neu.madcourse.wellness_studio.customCalendar.Property;
import edu.neu.madcourse.wellness_studio.friendsList.FriendsList;
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
    ImageButton settingBtn, profileLoginBtn;
    ImageView profileImgIV;
    TextView nicknameTV;
    CheckBox goalFinishedCB;
    BottomNavigationView bottomNavigationView;
    CustomCalendar customCalendar;

    // db
    protected AppDatabase db;

    // firebase auth
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;

    private AlertDialog dialog;

    // calendar
    Calendar calendar;
    protected static HashMap<Object, Property> propertyMap;
    protected static HashMap<Integer, Object> dateMap;
    protected int selected = -1;
    protected int selectedPrev = -1;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // initialize db instance
        db = AppDatabase.getDbInstance(this.getApplicationContext());

        // initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // initialize calendar instance, get currdate
        calendar = Calendar.getInstance();
        String currdate = Utils.getCurrentDate();

        // get VI components
        customCalendar = findViewById(R.id.custom_calendar);

        settingBtn = findViewById(R.id.imageButton_setting);
        profileLoginBtn = findViewById(R.id.imageButton_login);

        profileImgIV = findViewById(R.id.profile_img);
        nicknameTV = findViewById(R.id.nickname_profile);
        goalFinishedCB = findViewById(R.id.goal_finished_checker);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // set bottom nav, home as activated
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    goToHome();
                    return true;
                case R.id.nav_exercise:
                    goToLightExercise();
                    return true;
                case R.id.nav_sleep:
                    goToSleepGoal();
                    return true;
                case R.id.nav_leaderboard:
                    goToLeaderboard();
                    return true;
                default:
                    //Log.v(TAG, "Invalid bottom navigation item clicked.");
                    return false;
            }
        });

        // show username and profile img TODO: img
        nicknameTV.setText(UserService.getNickname(db));

        // set buttons
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

        // get a list of dates with finished goal status
        List<String> checkedDates = UserService.getFinishedDatesOfMonth(db, currdate.substring(0, 7));

        // mark checked date as property "CHECKED" (green)
        if (checkedDates != null) {
            for (String date : checkedDates) {
                Integer d = Integer.parseInt(date.substring(8));
                dateMap.put(d, CHECKED);
            }
        }
        // mark today as current
        dateMap.put(calendar.get(Calendar.DAY_OF_MONTH), CURRENT);

        // upload local weekly counts if online
        updateOnlineCounts();

        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this);
        customCalendar.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this);

        customCalendar.setDate(calendar, dateMap);

        // when a date is selected:
        // 1) mark different color for the selected date
        // 2) show goal status at the checkbox
        // 3) change the prev selected date back to some proper view
        customCalendar.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(View view, Calendar selectedDate, Object desc) {
                // get which date is selected
                int date = selectedDate.get(Calendar.DAY_OF_MONTH);
                int month = selectedDate.get(Calendar.MONTH) + 1;
                int year = selectedDate.get(Calendar.YEAR);
                String monthStr = to2CharString(month);
                // Log.v(TAG, "selected date: " + year + " | " + month + " | " + date);

                // get date key for checking db
                String dateKey = year + "-" + monthStr + "-" + to2CharString(date);
                //Log.v(TAG, "date key is : " + dateKey);
                //Log.v(TAG, "is finished : " + UserService.getGoalFinishedByDate(db, dateKey));

                // disable checkbox if not current month
                if (isCurrentMonth(dateKey) != 0) {
                    // disable checkbox
                    goalFinishedCB.setEnabled(false);
                    Utils.postToast("You can only change status of current month", Profile.this);

                } else {  // modifying current month
                    goalFinishedCB.setEnabled(true);
                    selected = date;
                    dateMap.put(selected, SELECTED);

                    // a future date of curr month is selected, show a short toast
                    if (isFuture(dateKey)) {
                        goalFinishedCB.setEnabled(false);
                        Utils.postToast("Note that you can not mark finished for a future date!", Profile.this);
                    }

                    // change prev selected view back
                    if (selectedPrev != (-1)) {
                        String prevDateKey = currdate.substring(0, 8) + to2CharString(selectedPrev);
                        // is selectedPrev current date?
                        if (selectedPrev == Integer.parseInt(currdate.substring(8))) {
                            dateMap.put(selectedPrev, CURRENT);
                        }
                        // is selectedPrev now finished?
                        else if (UserService.getGoalFinishedByDate(db, prevDateKey)) {
                            dateMap.put(selectedPrev, CHECKED);
                        }
                        else dateMap.put(selectedPrev, DEFAULT);
                    }

                    // mark selectedPrev and update calendar view
                    selectedPrev = selected;
                    customCalendar.setDate(calendar, dateMap);  // reset view
                }

                // set checkbox view every time a date is selected
                Boolean isFinished = UserService.getGoalFinishedByDate(db, dateKey);
                goalFinishedCB.setChecked(isFinished);

                // set checkbox listener, only approve change if it's current month and not future
                goalFinishedCB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // not current month or future, refuse, show toast
                        if (isCurrentMonth(dateKey) != 0 || isFuture(dateKey)) {
                            Utils.postToast("You can only change status of current month", Profile.this);
//                            UserService.updateExerciseGoalStatus(db, !isFinished, dateKey);
//                            String isFinishedStr = isFinished? "Not Finished" : "Finished";
//                            Utils.postToast("Change status of " + dateKey + " to " + isFinishedStr, Profile.this);
                            // reload calendar view

                        } else {  // current month
                            // approve change on goal status, update checker
                            goalFinishedCB.setChecked(!isFinished);
                            // update db
                            UserService.updateExerciseGoalStatus(db, !isFinished, dateKey);
                            String isFinishedStr = isFinished? "Not Finished" : "Finished";
                            Utils.postToast("Change status of " + dateKey + " to " + isFinishedStr, Profile.this);

                            // update online db if is at current week, if not ignore
                            if (isCurrentWeek(dateKey)) {
                                updateOnlineCounts();
                            }
                        }
                    }
                });

//                Snackbar.make(customCalendar, selectedDate.get(Calendar.DAY_OF_MONTH)
//                        + " / " + (selectedDate.get(Calendar.MONTH) + 1)
//                        + " / " + selectedDate.get(Calendar.YEAR)
//                        + " selected", Snackbar.LENGTH_LONG).show();
            }
        });

        // set checkbox and checkbox listener if no date is selected
        goalFinishedCB.setChecked(UserService.getGoalFinishedByDate(db, currdate));
        goalFinishedCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // change view by the prev view
                Boolean isFinished = UserService.getGoalFinishedByDate(db, currdate);
                goalFinishedCB.setChecked(!isFinished);

                // update db
                UserService.updateExerciseGoalStatus(db, !isFinished, currdate);
                String isFinishedStr = isFinished? "Not Finished" : "Finished";
                Utils.postToast("Change status of " + currdate + " to " + isFinishedStr, Profile.this);

                updateOnlineCounts();
                }
        });

        // login or logout
        profileLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get online status
                User user = UserService.getCurrentUser(db);
                assert user != null;
                if (user.hasLoggedInOnline) {
                    FirebaseAuth.getInstance().signOut();
                    profileLoginBtn.setImageResource(R.drawable.ic_baseline_login_24);
                    UserService.changeOnlineStatus(db);
                    Utils.postToast("Logged out.", Profile.this);
                } else {
                    // go to login screen
                    createLoginDialog();
                }
            }
        });
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
                    Log.v(TAG, "fuser: user is online");
                    profileLoginBtn.setImageResource(R.drawable.ic_baseline_logout_24);
                    UserService.setUserOnline(db);
                } else {
                    Log.v(TAG, "fuser: user is offline");
                    UserService.setUserOffline(db);
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
        String month = to2CharString(monthInt);

        String newYearMonth = year + "-" + month;
        // Log.v(TAG, "new year and month: " + newYearMonth);

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
        propertyMap.put(DEFAULT, propDefault);

        // initialize current date
        Property propCurrent = new Property();
        propCurrent.layoutResource = R.layout.current_view;
        propCurrent.dateTextViewResource = R.id.current_text;
        propertyMap.put(CURRENT, propCurrent);

        // checked date (when exercise goal finished)
        Property propChecked = new Property();
        propChecked.layoutResource = R.layout.checked_view;
        propChecked.dateTextViewResource = R.id.checked_text;
        propertyMap.put(CHECKED, propChecked);

        // selected date (when exercise goal finished)
        Property propSelected = new Property();
        propSelected.layoutResource = R.layout.selected_view;
        propSelected.dateTextViewResource = R.id.selected_text;
        propertyMap.put(SELECTED, propSelected);
    }


    // show a login dialog if user clicks login button
    public void createLoginDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.activity_login, null);

        Button loginButton= (Button) contactPopupView.findViewById(R.id.loginBtn);
        EditText emailTV = (EditText) contactPopupView.findViewById(R.id.email);
        EditText passwordTV = (EditText) contactPopupView.findViewById(R.id.password);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email = emailTV.getText().toString();
                String password = passwordTV.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Utils.postToast("Please enter email.", Profile.this);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Utils.postToast("Please enter password.", Profile.this);
                    return;
                }

                // Firebase auth
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(
                                            @NonNull Task<AuthResult> task)
                                    {
                                        if (task.isSuccessful()) {
                                            User user = UserService.getCurrentUser(db);
                                            assert user != null;
                                            user.setEmail(email);

                                            Utils.postToast("Login successful.", Profile.this);
                                            user.setHasLoggedInOnline(true);
                                            user.setHasOnlineAccount(true);
                                            UserService.updateUserInfo(db, user);
                                            profileLoginBtn.setImageResource(R.drawable.ic_baseline_logout_24);

                                            if (user.userId == null) {
                                                Log.v(TAG, "null uid, downloading info from db");
                                                loadUserInfoFromOnline(email, user);
                                            }

                                            // for test, load everytime
//                                            if (true) {
//                                                Log.v(TAG, "downloading info from db");
//                                                loadUserInfoFromOnline(email);
//                                            }

                                            dialog.dismiss();
                                        }

                                        else {
                                            Utils.postToast("Login failed.", Profile.this);
                                        }
                                    }
                                });
            }
        });
    }


    // transfer an integer to a 2-char string, add 0 before single digit number
    private String to2CharString(int num) {
        return num<10 ? "0"+num : ""+num;
    }


    // if user is online, update online db
    private void updateOnlineCounts() {
        if (Objects.requireNonNull(UserService.getCurrentUser(db)).getHasLoggedInOnline()) {
            int currWeeklyCounts = UserService.getWeeklyFinishedCount(db);
            UserService.updateWeeklyCounts(db, currWeeklyCounts);
        }
    }

    // parse date / month / year from a yyyy-mm-dd date
    private String getDate(String currdate) {
        return currdate.substring(8);
    }

    private String getMonth(String currdate) {
        return currdate.substring(5, 7);
    }

    private String getYear(String currdate) {
        return currdate.substring(0, 4);
    }

    // check if a date (yyyy-mm-dd) is future
    private Boolean isFuture(String date) {
        String[] elems = date.split("-");
        String[] currElems = Utils.getCurrentDate().split("-");
        int dateInt = Integer.parseInt(elems[0]) * 10000 +
                Integer.parseInt(elems[1]) * 100 +
                Integer.parseInt(elems[2]);
        int currDateInt = Integer.parseInt(currElems[0]) * 10000 +
                Integer.parseInt(currElems[1]) * 100 +
                Integer.parseInt(currElems[2]);
        return dateInt > currDateInt;
    }

    // check if a date is in current month
    // return -1 if past, 0 if current, 1 if future
    private int isCurrentMonth(String date) {
        String[] elems = date.split("-");
        String[] currElems = Utils.getCurrentDate().split("-");
        int dateInt = Integer.parseInt(elems[0]) * 100 +
                Integer.parseInt(elems[1]);
        int currDateInt = Integer.parseInt(currElems[0]) * 100 +
                Integer.parseInt(currElems[1]);
        int delta = dateInt - currDateInt;
        return Integer.compare(delta, 0);
    }

    // check if a date is in current week, called before update online db counts
    private boolean isCurrentWeek(String date) {
        String firstDayOfWeek = UserService.getFirstDayOfWeek();
        String[] firstDayElems = Utils.getCurrentDate().split("-");
        String[] elems = date.split("-");
        int firstDayInt = Integer.parseInt(firstDayElems[0]) * 10000 +
                Integer.parseInt(firstDayElems[1]) * 100 +
                Integer.parseInt(firstDayElems[2]);
        int dayInt = Integer.parseInt(elems[0]) * 10000 +
                Integer.parseInt(elems[1]) * 100 +
                Integer.parseInt(elems[2]);
        return dayInt >= firstDayInt && !isFuture(date);  // should not happen because we blocked future call
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfileImg(profileImgIV);
    }

    // load profile img from sdcard, if can't load from assets/
    private void loadProfileImg(ImageView imageView) {
        boolean res = UserService.loadImageForProfile(imageView);
        if (!res) {
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

    // download userinfo from firebase realtime db
    // called after login if no local uid is saved in local
    private void loadUserInfoFromOnline(String email, User user) {
        Log.v(TAG, "loading, using email: " + email);

        DatabaseReference dfb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference updateLocal = dfb.child("users");
        updateLocal.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Set uid given email
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String key = ds.getKey();
                    String emailFound = ds.child("email").getValue(String.class);

                    assert emailFound != null;
                    if (emailFound.equals(email)) {
                        user.setUserId(key);
                        UserService.updateUserInfo(db, user);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // ========   helpers to start new activity  ===================

    private void goToHome() {
        startActivity(new Intent(Profile.this, MainActivity.class));
    }

    private void goToLightExercise() {
        startActivity(new Intent(Profile.this, LightExercises.class));
    }

    private void goToSleepGoal() {
        startActivity(new Intent(Profile.this, WakeupSleepGoal.class));
    }

    private void goToLeaderboard() {
        startActivity(new Intent(Profile.this, Leaderboard.class));
    }
}