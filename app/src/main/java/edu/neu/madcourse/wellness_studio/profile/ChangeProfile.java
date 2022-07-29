package edu.neu.madcourse.wellness_studio.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import edu.neu.madcourse.wellness_studio.utils.Utils;
import localDatabase.AppDatabase;
import localDatabase.userInfo.User;

public class ChangeProfile extends AppCompatActivity {
    // test
    protected static String TAG = "setting";

    protected final static String NAME_HINT = "Username should only contains alphabet and digits.";
    protected final static String NAME_TOAST = "Please enter a valid username within 25 chars.";
    protected final static String MISS_INFO_TOAST = "Please enter both email and password to create account.";
    protected final static String INVALID_INFO_TOAST = "Please enter valid email and password";


    // VI
    ImageButton profileImgBtn, saveBtn, cancelBtn;
    EditText usernameInputET, emailInputET, passwordInputET;
    TextView usernameHintTV;
    ImageButton homeBtn, exerciseBtn, sleepBtn, leaderboardBtn;

    // db
    protected AppDatabase db;
    protected User user;

    // user input
    protected String nicknameInput;
    protected String emailInput;
    protected String passwordInput;
    protected Boolean hasChangedImg = false;

    // user info
    protected Boolean hasOnlineAccount = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);

        // initialize db instance
        db = AppDatabase.getDbInstance(this.getApplicationContext());

        // get VI components
        profileImgBtn = findViewById(R.id.imageButton_change_img);
        saveBtn = findViewById(R.id.imageButton_save);
        cancelBtn = findViewById(R.id.imageButton_cancel);

        usernameInputET = findViewById(R.id.username_input_ET);
        emailInputET = findViewById(R.id.email_input_ET);
        passwordInputET = findViewById(R.id.password_input_ET);
        usernameHintTV = findViewById(R.id.username_hint);

        homeBtn = findViewById(R.id.imageButton_home);
        exerciseBtn = findViewById(R.id.imageButton_exercise);
        sleepBtn = findViewById(R.id.imageButton_sleep);
        leaderboardBtn = findViewById(R.id.imageButton_leaderboard);

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
            passwordInputET.setText("******");
            emailInputET.setFocusable(false);
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
                        }
                    }
                }


                // in case both email and password have input, create account
                  // check if both valid, if not show toast
                  // create account

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

    private void goToProfile() {
        startActivity(new Intent(ChangeProfile.this, Profile.class));
        finish();
    }
}