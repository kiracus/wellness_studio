package edu.neu.madcourse.wellness_studio;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import localDatabase.AppDatabase;
import localDatabase.User;

public class Profile_With_Local_DB_Example extends AppCompatActivity {
    public TextView testTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_with_local_db_example);
        testTextView = findViewById(R.id.textViewInProfileTest2);
        //test user
        String nickname = "userNickName";
        Boolean hasOnlineAccount = true;
        String email = "email";
        String password = "password";
        Boolean hasLoggedInOnline = true;
        int userId = 123;
        String profileImg = "profileImg";
        String exerciseAlarm = "exerciseAlarm";
        String sleepAlarm = "sleepAlarm";
        String wakeUpAlarm = "wakeUpAlarm";
        saveNewUser(nickname, hasOnlineAccount, email, password, hasLoggedInOnline, userId, profileImg, exerciseAlarm, sleepAlarm, wakeUpAlarm);
        loadUserInfo();
    }


    public void loadUserInfo() {
        //step 1: first get a db instance
        AppDatabase appDatabase = AppDatabase.getDbInstance(this.getApplicationContext());
        //this corresponds to the UserDao class's getAllUser(L12) sql query, i'm doing 'select * from table'
        //as an example, you can create your own abstract method in the dao class by following the same pattern
        // and write your own sql query in the annotation above the method.
        List<User> userList = appDatabase.userDao().getAllUser();
        Log.d("myApp", "userList" + userList);
        for (User user : userList) {
            Log.d("myApp", "user nickname: " + user.nickname);
        }
        if (userList.size() != 0) {
            String nickname = userList.get(0).nickname;
            String email = userList.get(0).email;
            Boolean hasOnlineAccount = userList.get(0).hasOnlineAccount;
            String password = userList.get(0).password;
            Boolean hasLoggedInOnline = userList.get(0).hasLoggedInOnline;
            int userId = userList.get(0).userId;
            String profileImg = userList.get(0).profileImg;
            String exerciseAlarm = userList.get(0).exerciseAlarm;
            String sleepAlarm = userList.get(0).sleepAlarm;
            String wakeUpAlarm = userList.get(0).wakeUpAlarm;
            testTextView.setText("username: " + nickname + ", " + "email: " + email + ", " + "hasOnlineAccount: " + hasOnlineAccount + ", " +
                    "password: " + password + ", " + "hasLoggedInOnline: " + ", " + hasLoggedInOnline + "userId: " + userId + ", " +
                    "profileImg" + profileImg + ", " + "exerciseAlarm: " + exerciseAlarm + ", " + "sleepAlarm: " + sleepAlarm + ", " + "wakeUpAlarm: " + wakeUpAlarm + ", ");
        }

    }

    public void saveNewUser(String nickname, Boolean hasOnlineAccount, String email,
                            String password, Boolean hasLoggedInOnline, int userId, String profileImg,
                            String exerciseAlarm, String sleepAlarm, String wakeUpAlarm) {
        //step 1: to insert, first get a db instance
        AppDatabase appDatabase = AppDatabase.getDbInstance(this.getApplicationContext());
        // step 2: an entity object(the table)
        User user = new User();
        //step 3: fill in the column details of the new object
        user.nickname = nickname;
        user.hasOnlineAccount = hasOnlineAccount;
        user.email = email;
        user.password = password;
        user.hasLoggedInOnline = hasLoggedInOnline;
        user.userId = userId;
        user.profileImg = profileImg;
        user.exerciseAlarm = exerciseAlarm;
        user.sleepAlarm = sleepAlarm;
        user.wakeUpAlarm = wakeUpAlarm;
        //step 4: insert this new object into the database
        appDatabase.userDao().insertUser(user);
        //step 5: close this activity
        finish();
    }
}