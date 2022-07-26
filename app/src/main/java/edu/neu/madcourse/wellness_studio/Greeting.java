package edu.neu.madcourse.wellness_studio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import edu.neu.madcourse.wellness_studio.utils.UserService;
import edu.neu.madcourse.wellness_studio.utils.Utils;
import localDatabase.AppDatabase;

// user only enters this screen if db has no user info, will check userinfo when main starts
public class Greeting extends AppCompatActivity {
    // test
    private final static String TAG = "greet";

    // VI
    EditText enterNameET;
    Button startBtn;

    // user and db
    private String nicknameInput;
    protected AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        // initialize db instance
        db = AppDatabase.getDbInstance(this.getApplicationContext());

        enterNameET = findViewById(R.id.enter_name_ET);
        startBtn = findViewById(R.id.start_button);

        // if start button is clicked,
        // check if user has a valid input of nickname (only allow letter and number
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nicknameInput = enterNameET.getText().toString();

                // check if nickname is valid, if yes save nickname, if not send a toast
                if (checkValidNickname(nicknameInput)) {
                    // TODO save nickname in user class or db
                    UserService.createNewUser(db, nicknameInput);

                    // start main activity and finish current activity
                    startActivity(new Intent(Greeting.this, MainActivity.class));
                    finish();

                } else {
                    Utils.postToast("Please enter a valid username.", Greeting.this);
                };
            }
        });
    }

    // check if a string only contains alphabet and digit
    private boolean checkValidNickname(String s) {
        if (s == null || s.equals("")) {
            return false;
        } else {
            int len = s.length();
            for (int i = 0; i < len; i++) {
                if ((!Character.isLetterOrDigit(s.charAt(i)))) {
                    return false;
                }
            }
            return true;
        }

    }
}