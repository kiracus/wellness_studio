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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.neu.madcourse.wellness_studio.MainActivity;
import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.WakeupSleepGoal;
import edu.neu.madcourse.wellness_studio.friendsList.FriendsList;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.profile.ChangeProfile;
import edu.neu.madcourse.wellness_studio.profile.Profile;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import edu.neu.madcourse.wellness_studio.utils.Utils;
import localDatabase.AppDatabase;
import localDatabase.userInfo.User;

public class Leaderboard extends AppCompatActivity {
    // TAG for test
    private final static String TAG = "leaderboard";

    BottomNavigationView bottomNavigationView;
    ImageButton friendsList;
    ImageButton refreshBtn;
    TextView currentWeek;
    ImageView profileIV;

    protected AppDatabase db;
    protected User user;
    private FirebaseAuth mAuth;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    RecyclerView leaderboardRecyclerView;
    List<String> friendEmailList;
    List<String> friendWeeklyCount;
    ProgressBar weeklyProgressBar;
    LeaderboardAdapter leaderboardAdapter;

    String date;


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
        profileIV = findViewById(R.id.imageView_profile);

        profileIV.setOnClickListener(v -> startActivity(new Intent(Leaderboard.this, Profile.class)));
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

//        currentWeek.setText("Week from " + monthStart + "-" + dayStart + "-" + yearStart + " to " +
//                monthEnd + "-" + dayEnd + "-" + yearEnd);

        currentWeek.setText(monthStart + " / " + dayStart + " / " + yearStart + "  to  " +
                monthEnd + " / " + dayEnd + " / " + yearEnd);

        date = UserService.getFirstDayOfWeek();
        Log.d("FRIENDLIST", date);

        // Instantiate array lists
        friendEmailList = new ArrayList<>();
        friendWeeklyCount = new ArrayList<>();
        weeklyProgressBar = (ProgressBar) findViewById(R.id.weeklyProgressBar);

        // set view and adapter
        leaderboardRecyclerView = findViewById(R.id.weeklyRankingRecyclerView);
        leaderboardRecyclerView.setHasFixedSize(true);
        leaderboardAdapter = new LeaderboardAdapter(Leaderboard.this, friendEmailList, friendWeeklyCount);
        leaderboardRecyclerView.setAdapter(leaderboardAdapter);
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(Leaderboard.this));

        // Populate lists with cloud data
        DatabaseReference dbRoot = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbUserFriendsRef = dbRoot.child("users").child(user.userId).child("friends");

        dbUserFriendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // friendEmailList.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
//                    Log.d("FRIENDLIST", "key + ");
//                    Log.d("FRIENDLIST", ds.getKey());

                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference allUsers = db.child("users");
                    allUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds2 : snapshot.getChildren()) {
                                if (ds2.getKey().equals(ds.getKey())) {
                                    try {
                                        if (ds.child("shareTo").getValue(Boolean.class).equals(true)) {
                                            friendEmailList.add(ds2.child("name").getValue(String.class));
                                            Log.d("FRIENDLIST", ds2.getKey());
                                            // TODO figure this part out + delete follwing line of code
                                            friendWeeklyCount.add(String.valueOf(3));
                                            DatabaseReference db2 = FirebaseDatabase.getInstance().getReference();
                                            DatabaseReference getCounts = db2.child("weeklyOverviews").child(ds2.getKey());
                                            Log.d("DEBUG", String.valueOf(db2.child("weeklyOverviews").child(ds2.getKey())));
                                            getCounts.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    int count = 0;
                                                    for (DataSnapshot ds3 : snapshot.getChildren()) {
                                                        if (ds3.getKey().equals(date)) {
                                                            count = ds3.getValue(Integer.class);
                                                            Log.d("DEBUG", String.valueOf(count));
                                                            friendWeeklyCount.add(String.valueOf(count));
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(Leaderboard.this));
                                            leaderboardAdapter.notifyItemInserted(friendEmailList.size());
                                        }
                                    } catch (Exception e) {
                                        Log.d("Error", String.valueOf(e));
                                    }
                                }
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
    // Edit progress bar for each friend + count of days/7
    // Work on refresh button

    public void refreshLeaderboard() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }


    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.nav_leaderboard);
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