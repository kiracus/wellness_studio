package edu.neu.madcourse.wellness_studio.friendsList;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.wellness_studio.Greeting;
import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.WakeupSleepGoal;
import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import localDatabase.AppDatabase;
import localDatabase.userInfo.User;

public class FriendsList extends AppCompatActivity {
    ImageButton homeBtn, exerciseBtn, sleepBtn, leaderboardBtn;
    ToggleButton exerciseShareSetting;
    AppDatabase appDatabase;

    ListView friendListView;
    TextView friendUsername;

    ArrayList<String> friendUsernameList = new ArrayList<>();
    ArrayList<String> imgUrlList = new ArrayList<>();
    ArrayList<String> friendIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        homeBtn = findViewById(R.id.imageButton_home);
        exerciseBtn = findViewById(R.id.imageButton_exercise);
        sleepBtn = findViewById(R.id.imageButton_sleep);
        leaderboardBtn = findViewById(R.id.imageButton_leaderboard);
        exerciseShareSetting = findViewById(R.id.exerciseShareButton);

        friendUsername = findViewById(R.id.friendListUsername);
        friendListView = findViewById(R.id.friendsListRecyclerView);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(FriendsList.this, Greeting.class)));
        exerciseBtn.setOnClickListener(v -> startActivity(new Intent(FriendsList.this, LightExercises.class)));
        sleepBtn.setOnClickListener(v -> startActivity(new Intent(FriendsList.this, WakeupSleepGoal.class)));
        leaderboardBtn.setOnClickListener(v -> startActivity(new Intent(FriendsList.this, Leaderboard.class)));

        FriendListAdapter friendListAdapter = new FriendListAdapter(this, imgUrlList, friendUsernameList);
        friendListView.setAdapter(friendListAdapter);

        // Local db
        appDatabase = AppDatabase.getDbInstance(this.getApplicationContext());
        User user = UserService.getCurrentUser(appDatabase);
        assert user != null;
        String userId = String.valueOf(user.userId);

        // Cloud
        DatabaseReference dbRoot = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbUserFriendsRef = dbRoot.child("users").child(userId).child("friends");

        dbUserFriendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    friendIdList.add(ds.getValue().toString());
                    List<User> userList = appDatabase.userDao().getAllUser();
                    for (int i = 0; i < userList.size(); i++) {
                        if (ds.getValue().toString().equals(String.valueOf(userList.get(i).userId))) {
                            friendUsernameList.add(userList.get(i).nickname);
                            if (userList.get(i).profileImg != null) {
                                imgUrlList.add(userList.get(i).profileImg);
                            }
                        }
                    }
                }
                friendListAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
