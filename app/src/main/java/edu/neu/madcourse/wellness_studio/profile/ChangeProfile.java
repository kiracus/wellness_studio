package edu.neu.madcourse.wellness_studio.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import localDatabase.AppDatabase;
import localDatabase.userInfo.User;

public class ChangeProfile extends AppCompatActivity {
    // test
    protected static String TAG = "setting";

    // VI
    ImageButton profileImgBtn, saveBtn, cancelBtn;
    EditText usernameInputET, emailInputET, passwordInputET;
    TextView usernameHintTV;
    ImageButton homeBtn, exerciseBtn, sleepBtn, leaderboardBtn;

    // db
    protected AppDatabase db;

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


        // select image, process img and save in local, show img, flag img changed
        profileImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show prompt to select new img

                // process img to 100*100

                // save img in local

                // img changed flag on
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if img changed, if yes change db

                // in case user name has input, update local username in db
                  // check if username has input
                  // check username valid, if not show hint and toast


                // check if email and password has input
                // in case either email or password has input, show toast and ignore input


                // in case both email and password have input, create account
                  // check if both valid, if not show toast
                  // create account
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if img changed flag on, if yes discard saved img

                // go back to profile activity, finish current activity, return
            }
        });
    }
}