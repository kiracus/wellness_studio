package edu.neu.madcourse.wellness_studio;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import localDatabase.AppDatabase;
import localDatabase.userInfo.User;

public class Profile_With_Local_DB_Example extends AppCompatActivity {
    public TextView testTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_with_local_db_example);

        testTextView = findViewById(R.id.textViewInProfileTest2);


        if(getCurrentUser() == null) {
            User user = new User();
            createNewUser(user);
        }
         loadUserInfo();


        //update user test info
        User currentUser = getCurrentUser();
        currentUser.setNickname("toby");
        currentUser.setEmail("toby@email.com");
        currentUser.setPassword("xxxx");

        updateUserInfo(currentUser);

        loadUserInfo();
    }

    public User getCurrentUser() {
        //step 1: first get a db instance
        AppDatabase appDatabase = AppDatabase.getDbInstance(this.getApplicationContext());

        //this corresponds to the UserDao class's getAllUser(L12) sql query, i'm doing 'select * from table'
        //as an example, you can create your own abstract method in the dao class by following the same pattern
        // and write your own sql query in the annotation above the method.
        List<User> userList = appDatabase.userDao().getAllUser();
        if(userList.size() != 0) {
            return userList.get(0);
        }
        else
            return null;
    }
    public void createNewUser(User user) {
        //step 1: to insert, first get a db instance
        AppDatabase appDatabase = AppDatabase.getDbInstance(this.getApplicationContext());
        //step 2: implement the insertUser from UserDao. class
        appDatabase.userDao().insertUser(user);
        //step 2: close this activity
//        finish();
    }

    public void updateUserInfo(User user) {
        AppDatabase appDatabase = AppDatabase.getDbInstance(this.getApplicationContext());
        appDatabase.userDao().updateUser(user);
    }

    public void loadUserInfo() {
        User user = getCurrentUser();
        int user_info_id = user.getUser_info_id();
        String nickname = user.nickname;
        String email = user.email;
        Boolean hasOnlineAccount = user.hasOnlineAccount;
        String password = user.password;
        Boolean hasLoggedInOnline = user.hasLoggedInOnline;
        int userId = user.userId;
        String profileImg = user.profileImg;
        String exerciseAlarm = user.exerciseAlarm;
        String sleepAlarm = user.sleepAlarm;
        String wakeUpAlarm = user.wakeUpAlarm;
        testTextView.setText("user_info_id: " + user_info_id + ", " + "username: " + nickname + ", "
                + "email: " + email + ", " + "hasOnlineAccount: " + hasOnlineAccount + ", "
                + "password: " + password + ", " + "hasLoggedInOnline: " + ", "
                + hasLoggedInOnline + "userId: " + userId + ", "
                + "profileImg" + profileImg + ", " + "exerciseAlarm: "
                + exerciseAlarm + ", " + "sleepAlarm: " + sleepAlarm + ", "
                + "wakeUpAlarm: " + wakeUpAlarm + ", ");
    }
}