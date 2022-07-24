package edu.neu.madcourse.wellness_studio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import localDatabase.AppDatabase;
import localDatabase.User;

public class Profile extends AppCompatActivity {
    public TextView testTextView;
    public EditText editTextTextPersonName;
    public Button testButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        testTextView = findViewById(R.id.textViewInProfileTest2);
        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        testButton = findViewById(R.id.testButton);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewUser(editTextTextPersonName.getText().toString(),true,editTextTextPersonName.getText().toString());
                loadUserInfo();
            }
        });
    }


    public void loadUserInfo() {
        //step 1: first get a db instance
        AppDatabase appDatabase = AppDatabase.getDbInstance(this.getApplicationContext());
        //this corresponds to the UserDao class's getAllUser(L12) sql query, i'm doing 'select * from table'
        //as an example, you can create your own abstract method in the dao class by following the same pattern
        // and write your own sql query in the annotation above the method.
        List<User> userList = appDatabase.userDao().getAllUser();
        if(userList.size() != 0) {
            String nickname =userList.get(0).nickname;
            String email = userList.get(0).email;
            Boolean hasOnlineAccount = userList.get(0).hasOnlineAccount;
            testTextView.setText("username: " + nickname +", "+ "email: " + email + ", " + "hasOnlineAccount: " + hasOnlineAccount);
        }

    }

    public void saveNewUser(String nickname, Boolean hasOnlineAccount, String email) {
        //step 1: to insert, first get a db instance
        AppDatabase appDatabase = AppDatabase.getDbInstance(this.getApplicationContext());
       // step 2: an entity object(the table)
        User user = new User();
        //step 3: fill in the column details of the new object
        user.nickname = nickname;
        user.hasOnlineAccount = hasOnlineAccount;
        user.email = email;
        //step 4: insert this new object into the database
        appDatabase.userDao().insertUser(user);
        //step 5: close this activity
        finish();
    }
}