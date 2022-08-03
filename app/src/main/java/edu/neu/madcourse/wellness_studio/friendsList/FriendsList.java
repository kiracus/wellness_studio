package edu.neu.madcourse.wellness_studio.friendsList;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.neu.madcourse.wellness_studio.Greeting;
import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.WakeupSleepGoal;
import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import edu.neu.madcourse.wellness_studio.utils.Utils;
import localDatabase.AppDatabase;
import localDatabase.userInfo.User;

public class FriendsList extends AppCompatActivity {
    ImageButton homeBtn, exerciseBtn, sleepBtn, leaderboardBtn;
    ToggleButton exerciseShareSetting;
    AppDatabase appDatabase;

    ListView friendListView;
    TextView friendUsername;
    public String friendEmailData = "";
    String userIdFriend = "";
    String userId = "";
    FriendListAdapter friendListAdapter;


    ArrayList<String> friendEmailList = new ArrayList<>();
    ArrayList<String> imgUrlList = new ArrayList<>();
    ArrayList<String> friendIdList = new ArrayList<>();

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private final static String ADD_FRIEND_ERROR_MSG =
            "Error adding friend. Please try again.";
    private final static String ADD__DUPLICATE_FRIEND_ERROR_MSG =
            "You are already friends with that user.";
    private final static String ADD__INVALID_FRIEND_ERROR_MSG =
            "That user does not exist.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        homeBtn = findViewById(R.id.imageButton_home);
        exerciseBtn = findViewById(R.id.imageButton_exercise);
        sleepBtn = findViewById(R.id.imageButton_sleep);
        leaderboardBtn = findViewById(R.id.imageButton_leaderboard);
        exerciseShareSetting = findViewById(R.id.exerciseShareButton);
        FloatingActionButton addFriend = findViewById(R.id.add_friend);

        friendUsername = findViewById(R.id.friendListUsername);
        friendListView = findViewById(R.id.friendsListRecyclerView);

        homeBtn.setOnClickListener(v -> startActivity(new Intent(FriendsList.this, Greeting.class)));
        exerciseBtn.setOnClickListener(v -> startActivity(new Intent(FriendsList.this, LightExercises.class)));
        sleepBtn.setOnClickListener(v -> startActivity(new Intent(FriendsList.this, WakeupSleepGoal.class)));
        leaderboardBtn.setOnClickListener(v -> startActivity(new Intent(FriendsList.this, Leaderboard.class)));

        friendListAdapter = new FriendListAdapter(this, imgUrlList, friendEmailList);
        friendListView.setAdapter(friendListAdapter);

        // Local db
        appDatabase = AppDatabase.getDbInstance(this.getApplicationContext());
        User user = UserService.getCurrentUser(appDatabase);
        assert user != null;
        userId = user.userId;

        // Cloud
        DatabaseReference dbRoot = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbUserFriendsRef = dbRoot.child("users").child(userId).child("friends");

        dbUserFriendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    friendIdList.add(ds.getKey());
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    Log.d("FRIENDLIST", "key + ");
                    Log.d("FRIENDLIST", ds.getKey());

                    DatabaseReference allUsers = db.child("users");
                    allUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds2 : snapshot.getChildren()) {
//                                Log.d("FRIENDLIST", "key 2 + ");
//                                Log.d("FRIENDLIST", ds2.getKey());
                                if (ds2.getKey().equals(ds.getKey())) {
                                    friendEmailList.add(ds2.child("email").getValue(String.class));
//                                    Log.d("FRIENDLIST", "EMAIL GENERATED  + ");
//                                    Log.d("FRIENDLIST", ds2.child("email").getValue(String.class));
                                    imgUrlList.add(ds2.child("img").getValue(String.class));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                friendListAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        addFriend.setOnClickListener(v -> createAddFriendDialog());

    }

    // add friend pop up window
    public void createAddFriendDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View contactPopupView = getLayoutInflater().inflate(R.layout.activity_add_friend, null);

        // EditText
        EditText friendEmailAddress = contactPopupView.findViewById(R.id.user_add_friend);

        // Button
        Button addFriendButton = contactPopupView.findViewById(R.id.add_friend_confirm_button);
        Button cancelAddFriendButton = contactPopupView.findViewById(R.id.add_friend_cancel_button);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        DatabaseReference dbRoot = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbUserRef = dbRoot.child("users");
        DatabaseReference getFriends = dbRoot.child("users");


        /// iterate over cloud db and find friend id by email
        /// add id to friend list
        addFriendButton.setOnClickListener(v -> {
            friendEmailData = friendEmailAddress.getText().toString();
            String createEmailOnData = friendEmailData.toLowerCase();
            getFriends.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds1 : snapshot.getChildren()) {
                        String key = ds1.getKey();
//                        Log.d("FRIENDLIST", "key + ");
//                        Log.d("FRIENDLIST", key);
                        String email = ds1.child("email").getValue(String.class);

//                        Log.d("FRIENDLIST", "email + ");
//                        Log.d("FRIENDLIST", email);

                        if (email.equals(createEmailOnData)) {
                            userIdFriend = key;
//                            Log.d("FRIENDLIST", "userIdFriend + ");
//                            Log.d("FRIENDLIST", userIdFriend);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if (createEmailOnData.equals("")) {
                errorAddFriend();
            } else if (!Objects.equals(userIdFriend, "") && friendIdList.contains(String.valueOf(userIdFriend))) {
                errorDuplicateFriend();
            } else {
                dbUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Checks if friend exists
                        if (!snapshot.child(String.valueOf(userIdFriend)).exists()) {
                            errorAddInvalidFriend();
                        }
                        else {
                            dbUserRef.child(userId)
                                    .child("friends")
                                    .child(userIdFriend).setValue("");
                            dbUserRef.child(userId)
                                            .child("friends")
                                            .child(userIdFriend)
                                            .child("shareTo").setValue(true);
                            dbUserRef.child(userId)
                                    .child("friends")
                                    .child(userIdFriend)
                                    .child("shareFrom").setValue(true);
                            Utils.postToast("Successfully added friend: " + createEmailOnData, FriendsList.this);
                            dialog.dismiss();
                        }
                        friendListAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }


        });
        cancelAddFriendButton.setOnClickListener(v -> cancelAddFriend());
    }

    private void cancelAddFriend() {
        dialog.dismiss();
        Utils.postToast("Add friend cancelled.", FriendsList.this);
    }

    private void errorAddFriend() {
        Utils.postToast(ADD_FRIEND_ERROR_MSG, FriendsList.this);
    }

    private void errorAddInvalidFriend() {
        Utils.postToast(ADD__INVALID_FRIEND_ERROR_MSG, FriendsList.this);
    }

    private void errorDuplicateFriend() {
        Utils.postToast(ADD__DUPLICATE_FRIEND_ERROR_MSG, FriendsList.this);
    }

    // TODO
    // Delete friend
    // Handle share/unshare of exercise w/friends (multiple listeners on list view buttons)
}
