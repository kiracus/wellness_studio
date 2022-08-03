package edu.neu.madcourse.wellness_studio.leaderboard;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

import edu.neu.madcourse.wellness_studio.Greeting;
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

    ImageButton homeBtn, exerciseBtn, sleepBtn, leaderboardBtn, friendsList;
    Button refreshBtn;
    TextView currentWeek;
    private EditText emailTextView, passwordTextView;
    private Button loginButton;

    protected AppDatabase db;
    protected User user;
    private FirebaseAuth mAuth;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        mAuth = FirebaseAuth.getInstance();
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);

        // Get current user
        db = AppDatabase.getDbInstance(this.getApplicationContext());
        user = UserService.getCurrentUser(db);

        homeBtn = findViewById(R.id.imageButton_home);
        exerciseBtn = findViewById(R.id.imageButton_exercise);
        sleepBtn = findViewById(R.id.imageButton_sleep);
        leaderboardBtn = findViewById(R.id.imageButton_leaderboard);
        friendsList = findViewById(R.id.go_to_friends);
        currentWeek = findViewById(R.id.currentWeek);
        refreshBtn = findViewById(R.id.refresh_leaderboard);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, Greeting.class)));

        exerciseBtn.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, LightExercises.class)));

        sleepBtn.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, WakeupSleepGoal.class)));

        // leaderboardBtn.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, Leaderboard.class)));
        friendsList.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, FriendsList.class)));

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
        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
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

}