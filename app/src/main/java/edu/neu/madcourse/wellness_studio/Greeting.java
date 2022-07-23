package edu.neu.madcourse.wellness_studio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import edu.neu.madcourse.wellness_studio.utils.Utils;

public class Greeting extends AppCompatActivity {

    EditText enterNameET;
    Button startBtn;

    private String nicknameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

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

                } else {
                    Utils.postToast("Please enter a valid username.", Greeting.this);
                };
            }
        });
    }

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