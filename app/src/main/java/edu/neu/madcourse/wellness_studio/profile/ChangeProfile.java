package edu.neu.madcourse.wellness_studio.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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


import edu.neu.madcourse.wellness_studio.Greeting;
import edu.neu.madcourse.wellness_studio.MainActivity;
import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.WakeupSleepGoal;
import edu.neu.madcourse.wellness_studio.friendsList.FriendsList;
import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import edu.neu.madcourse.wellness_studio.utils.Utils;
import localDatabase.AppDatabase;
import localDatabase.userInfo.User;

public class ChangeProfile extends AppCompatActivity {
    // test
    protected static String TAG = "setting";

    protected final static String NAME_HINT = "Username should only contain alphabet and digits.";
    protected final static String NAME_TOAST = "Please enter a valid username within 25 chars.";
    protected final static String MISS_INFO_TOAST = "Please enter both email and password to create account.";
    protected final static String INVALID_INFO_TOAST = "Please enter valid email and password.";
    protected final static String AUTH_INFO_SAVED = "Saved successfully.";
    protected final static String AUTH_INFO_NOT_SAVED = "Saved failed. Try again later.";


    // VI
    ImageButton profileImgBtn, saveBtn, cancelBtn;
    EditText usernameInputET, emailInputET, passwordInputET;
    TextView usernameHintTV;
    BottomNavigationView bottomNavigationView;

    // db
    protected AppDatabase db;
    protected User user;

    // Cloud
    DatabaseReference dbRoot = FirebaseDatabase.getInstance().getReference();
    DatabaseReference UsersRef = dbRoot.child("users");
    String uid = "";

    // Firebase Auth
    private FirebaseAuth mAuth;

    // user input
    protected String nicknameInput;
    protected String emailInput;
    protected String passwordInput;
    protected Boolean hasChangedImg = false;

    // user info
    protected Boolean hasOnlineAccount = false;

    // current week
    String date;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        // initialize db instance
        db = AppDatabase.getDbInstance(this.getApplicationContext());

        // initialize auth instance
        mAuth = FirebaseAuth.getInstance();

        // get VI components
        profileImgBtn = findViewById(R.id.imageButton_change_img);
        saveBtn = findViewById(R.id.imageButton_save);
        cancelBtn = findViewById(R.id.imageButton_cancel);

        usernameInputET = findViewById(R.id.username_input_ET);
        emailInputET = findViewById(R.id.email_input_ET);
        passwordInputET = findViewById(R.id.password_input_ET);
        usernameHintTV = findViewById(R.id.username_hint);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        // set bottom nav, should have no activated item
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(false); // TODO bug here
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
                    Log.v(TAG, "Invalid bottom navigation item clicked.");
                    return false;
            }
        });
        // get current week
        date = UserService.getFirstDayOfWeek();


        // show username
        usernameInputET.setText(UserService.getNickname(db));

        // check if user already has online account if yes show email and password as dots
        // and disable input area for email and password
        user = UserService.getCurrentUser(db);
        assert user != null;

        if (user.getHasOnlineAccount() != null && user.getHasOnlineAccount()) {
            hasOnlineAccount = true;
            emailInputET.setText(user.getEmail());
            emailInputET.setFocusable(false);
            emailInputET.setTextColor(Color.GRAY);
            passwordInputET.setText("******");
            passwordInputET.setFocusable(false);
            passwordInputET.setTextColor(Color.GRAY);
        }

        // TODO set profile pic, if none use some default img from assets


        // select image, process img and save in local, show img, flag img changed
        profileImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show prompt to select new img

                // process img to 100*100

                // save img in local

                // img changed flag on
                hasChangedImg = true;
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                // check if img changed, if yes change db

                // in case user name has input, check if it's valid
                nicknameInput = usernameInputET.getText().toString();
                if (!nicknameInput.equals(user.nickname)) {  // name is changed
                    if (!nicknameInput.equals("") && Utils.checkValidName(nicknameInput)) {
                        // ignore no input(""), user just deleted the original text
                        user.nickname = nicknameInput;
                    } else if (!nicknameInput.equals("") && !Utils.checkValidName(nicknameInput)) {
                        // not empty and not valid (null or has special chars)
                        Utils.postToast(NAME_TOAST, ChangeProfile.this);
                        usernameHintTV.setText(NAME_HINT);
                    }
                }

                // check if email and password has input
                // in case either email or password has input, show toast and ignore input
                if (!hasOnlineAccount) {
                    emailInput = emailInputET.getText().toString();
                    passwordInput = passwordInputET.getText().toString();

                    if (emailInput.equals("") && passwordInput.equals("")) {
                        // no input, ignore
                    } else if (emailInput.equals("") || passwordInput.equals("")) {
                        // only one has input
                        Utils.postToast(MISS_INFO_TOAST, ChangeProfile.this);
                    } else {  // both have input, check if valid
                        if (!Utils.checkValidEmail(emailInput) || !Utils.checkValidPassword(passwordInput)) {
                            Utils.postToast(INVALID_INFO_TOAST, ChangeProfile.this);
                        } else {
                            // valid, update user info, create account
                            // online account
                            user.setEmail(emailInput);
                            user.setPassword(passwordInput); // TODO save a token?
                            user.setHasOnlineAccount(true);

                            // Create user with Firebase Auth
                            mAuth.createUserWithEmailAndPassword(user.email, user.password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task)
                                        {
                                            if (task.isSuccessful()) {
                                                Utils.postToast(AUTH_INFO_SAVED, ChangeProfile.this);
                                            }
                                            else {
                                                Utils.postToast(AUTH_INFO_NOT_SAVED, ChangeProfile.this);
                                            }
                                        }
                            });

                            // Create online id and pass to save
                            UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // can't change, only update once if new online account created
                                    DatabaseReference saveKey = UsersRef.push();
                                    uid = saveKey.getKey();
                                    DatabaseReference newUserRef = UsersRef.child(uid);
                                    newUserRef.child("name").setValue(user.nickname);
                                    newUserRef.child("email").setValue(user.email);
                                    newUserRef.child("img").setValue(user.profileImg);
                                    newUserRef.child("friends").setValue("");

                                    // Create weeklyOverview field for user
                                    DatabaseReference dbOverview = FirebaseDatabase.getInstance().getReference();
                                    DatabaseReference weeklyOverviews = dbOverview.child("weeklyOverviews");
                                    weeklyOverviews.child(uid).child(date).setValue(0);

                                    // Set online ID in local db
                                    user.setUserId(uid);

                                    // Log user in online upon account creation
                                    mAuth.signInWithEmailAndPassword(user.email, user.password);
                                    user.setHasLoggedInOnline(true);
                                    UserService.updateUserInfo(db, user);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }

                // Write to cloud
                UsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // can change, update everytime profile data saved
                        DatabaseReference saveUser = UsersRef.child(user.userId);
                        saveUser.child("name").setValue(user.nickname);
                        saveUser.child("img").setValue(user.profileImg);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // update local use info
                UserService.updateUserInfo(db, user);
                goToProfile();
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if img changed flag on, if yes discard saved img
                if (hasChangedImg) {

                }
                // go back to profile activity, finish current activity, return
                goToProfile();
                finish();
            }
        });
    }

    // ========   helpers to start new activity  ===================

    private void goToHome() {
        startActivity(new Intent(ChangeProfile.this, MainActivity.class));
    }

    private void goToLightExercise() {
        startActivity(new Intent(ChangeProfile.this, LightExercises.class));
    }

    private void goToSleepGoal() {
        startActivity(new Intent(ChangeProfile.this, WakeupSleepGoal.class));
    }

    private void goToLeaderboard() {
        startActivity(new Intent(ChangeProfile.this, Leaderboard.class));
    }

    private void goToProfile() {
        startActivity(new Intent(ChangeProfile.this, Profile.class));
    }
}