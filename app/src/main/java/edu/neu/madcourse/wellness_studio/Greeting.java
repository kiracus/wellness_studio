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
    private final static String NAME_TOAST = "Please enter a valid username within 25 chars.";

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
                if (Utils.checkValidName(nicknameInput)) {
                    // create new user with input nickname
                    UserService.createNewUser(db, nicknameInput);

                    // start main activity and finish current activity
                    startActivity(new Intent(Greeting.this, MainActivity.class));
                    finish();

                } else {
                    Utils.postToast(NAME_TOAST, Greeting.this);
                };
            }
        });
    }


}