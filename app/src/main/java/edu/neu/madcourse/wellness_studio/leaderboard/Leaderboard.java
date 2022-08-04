package edu.neu.madcourse.wellness_studio.leaderboard;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

import edu.neu.madcourse.wellness_studio.Greeting;
import edu.neu.madcourse.wellness_studio.MainActivity;
import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.WakeupSleepGoal;
import edu.neu.madcourse.wellness_studio.friendsList.FriendsList;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.profile.ChangeProfile;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import edu.neu.madcourse.wellness_studio.utils.Utils;
import localDatabase.AppDatabase;
import localDatabase.userInfo.User;

public class Leaderboard extends AppCompatActivity {
    // TAG for test
    private final static String TAG = "leaderboard";

    BottomNavigationView bottomNavigationView;
    ImageButton friendsList;
    Button refreshBtn;
    TextView currentWeek;

    protected AppDatabase db;
    protected User user;
    private FirebaseAuth mAuth;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;


    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        mAuth = FirebaseAuth.getInstance();

        // Get current user
        db = AppDatabase.getDbInstance(this.getApplicationContext());
        user = UserService.getCurrentUser(db);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        friendsList = findViewById(R.id.go_to_friends);
        currentWeek = findViewById(R.id.currentWeek);
        refreshBtn = findViewById(R.id.refresh_leaderboard);

        friendsList.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, FriendsList.class)));

        // set bottom nav, currently at leaderboard so disable home item
        bottomNavigationView.setSelectedItemId(R.id.nav_leaderboard);
        bottomNavigationView.getMenu().findItem(R.id.nav_leaderboard).setEnabled(false);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    goToHome();
                    return false;
                case R.id.nav_exercise:
                    goToLightExercise();
                    return true;
                case R.id.nav_sleep:
                    goToSleepGoal();
                    return true;
                case R.id.nav_leaderboard:
                    return false;   // should not happen, disabled
                default:
                    Log.v(TAG, "Invalid bottom navigation item clicked.");
                    return false;
            }
        });

        assert user != null;
        if (user.hasOnlineAccount) {
            // User has logged in
            if (user.getHasLoggedInOnline()) {
                // Continue on to leaderboard
            }
            // User has not logged in, open login dialogue
            else {
                // Online login dialogue with Auth
                createLoginDialog();
            }
        } else {
            // Direct user to profile settings to create online account
            Utils.postToastLong("Create an online account to access the leaderboard!", Leaderboard.this);
            startActivity(new Intent(Leaderboard.this, ChangeProfile.class));
        }

        refreshBtn.setOnClickListener(v -> refreshLeaderboard());

        Calendar mCalendar = Calendar.getInstance();

        // Set first day of week
        mCalendar.set(Calendar.DAY_OF_WEEK, 1);
        int monthStart = mCalendar.get(Calendar.MONTH) + 1;
        int dayStart = mCalendar.get(Calendar.DAY_OF_MONTH);
        int yearStart = mCalendar.get(Calendar.YEAR);

        // Set last day of week
        mCalendar.set(Calendar.DAY_OF_WEEK, 7);
        int monthEnd = mCalendar.get(Calendar.MONTH) + 1;
        int dayEnd = mCalendar.get(Calendar.DAY_OF_MONTH);
        int yearEnd = mCalendar.get(Calendar.YEAR);

        currentWeek.setText("Week from " + monthStart + "-" + dayStart + "-" + yearStart + " to " +
                monthEnd + "-" + dayEnd + "-" + yearEnd);

    }

    public void createLoginDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
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
                    Utils.postToast("Please enter email.", Leaderboard.this);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Utils.postToast("Please enter password.", Leaderboard.this);
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
                                            Utils.postToast("Login successful.", Leaderboard.this);
                                            user.setHasLoggedInOnline(true);
                                            UserService.updateUserInfo(db, user);
                                            dialog.dismiss();
                                        }

                                        else {
                                            Utils.postToast("Login failed.", Leaderboard.this);
                                        }
                                    }
                                });
            }
        });
    }

    // TODO
    // Add leaderboard list view once login confirmed
    // Edit progress bar for each friend + count of days/7
    // Work on refresh button

    public void refreshLeaderboard() {
    }


    // ========   helpers to start new activity  ===================

    private void goToHome() {
        startActivity(new Intent(Leaderboard.this, MainActivity.class));
    }

    private void goToSleepGoal() {
        startActivity(new Intent(Leaderboard.this, WakeupSleepGoal.class));
    }

    private void goToLightExercise() {
        startActivity(new Intent(Leaderboard.this, LightExercises.class));
    }

}