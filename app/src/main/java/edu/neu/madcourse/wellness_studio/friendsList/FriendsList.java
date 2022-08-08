package edu.neu.madcourse.wellness_studio.friendsList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.neu.madcourse.wellness_studio.MainActivity;
import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.WakeupSleepGoal;
import edu.neu.madcourse.wellness_studio.leaderboard.Leaderboard;
import edu.neu.madcourse.wellness_studio.lightExercises.LightExercises;
import edu.neu.madcourse.wellness_studio.profile.Profile;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import edu.neu.madcourse.wellness_studio.utils.Utils;
import localDatabase.AppDatabase;
import localDatabase.userInfo.User;

public class FriendsList extends AppCompatActivity {
    // test
    private final static String TAG = "friend";

    BottomNavigationView bottomNavigationView;
    ImageView profileIV;
    ImageButton addFriend;
    ToggleButton exerciseShareSetting;
    AppDatabase appDatabase;

    RecyclerView friendListRecyclerView;
    FriendListAdapter friendListAdapter;
    List<String> friendEmailList;
    List<String> userList;

    public String friendEmailData = "";
    String userIdFriend = "";
    String userId = "";


    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private final static String ADD_FRIEND_ERROR_MSG =
            "Error adding friend. Please try again.";
    private final static String ADD__DUPLICATE_FRIEND_ERROR_MSG =
            "You are already friends with that user.";
    private final static String ADD__INVALID_FRIEND_ERROR_MSG =
            "That user does not exist.";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        // get VI components
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        exerciseShareSetting = findViewById(R.id.exerciseShareButton);
        addFriend = findViewById(R.id.add_friend);
        profileIV = findViewById(R.id.imageView_profile);

        profileIV.setOnClickListener(v -> startActivity(new Intent(FriendsList.this, Profile.class)));

        // set bottom nav, leaderboard as activated
        bottomNavigationView.setSelectedItemId(R.id.nav_leaderboard);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    goToHome();
                    return true;
                case R.id.nav_exercise:
                    goToLightExercise();
                    return true;
                case R.id.nav_sleep:
                    goToSleepGoal();
                    return true;
                case R.id.nav_leaderboard:
                    goToLeaderboard();
                    return true;
                default:
                    Log.v(TAG, "Invalid bottom navigation item clicked.");
                    return false;
            }
        });

        friendEmailList = new ArrayList<>();
        userList = new ArrayList<>();
        friendListRecyclerView = findViewById(R.id.friendsListRecyclerView);
        friendListRecyclerView.setHasFixedSize(true);
        friendListAdapter = new FriendListAdapter(FriendsList.this, friendEmailList);
        friendListRecyclerView.setAdapter(friendListAdapter);
        friendListRecyclerView.setLayoutManager(new LinearLayoutManager(FriendsList.this));

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
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    Log.d("FRIENDLIST", "key + ");
                    Log.d("FRIENDLIST", ds.getKey());

                    DatabaseReference allUsers = db.child("users");
                    allUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds2 : snapshot.getChildren()) {
                                if (ds2.getKey().equals(ds.getKey())) {
                                    friendEmailList.add(ds2.child("email").getValue(String.class));
                                    friendListRecyclerView.setLayoutManager(new LinearLayoutManager(FriendsList.this));
                                    friendListAdapter.notifyItemInserted(friendEmailList.size());

                                }
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference getAllUsers = db.child("users");
        getAllUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dss: snapshot.getChildren()) {
                    userList.add(dss.child("email").getValue(String.class));
                }
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
        DatabaseReference dbRoot2 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference getFriends = dbRoot.child("users");
        DatabaseReference dbUserRef = dbRoot2.child("users");


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
                        String email = ds1.child("email").getValue(String.class);
                        userList.add(ds1.child("email").getValue(String.class));

                        assert email != null;
                        if (email.equals(createEmailOnData)) {
                            userIdFriend = key;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // Make sure input isn't blank
            if (createEmailOnData.equals("")) {
                dialog.dismiss();
                errorAddFriend();
            }

            // Make sure email provided belongs to valid account
            else if (!userList.contains(createEmailOnData)) {
                dialog.dismiss();
                errorAddInvalidFriend();
            }
            else {
                dbUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean userFound = false;
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            if (ds.getKey().equals(userIdFriend)) {
                                userFound = true;
                            }
                        }
                        if (!userFound) {
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
                            friendEmailList.add(createEmailOnData);
                            dialog.dismiss();
                            friendListAdapter.notifyItemInserted(friendEmailList.size());
                        }
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
        Utils.postToastLong("Add friend cancelled.", FriendsList.this);
    }

    private void errorAddFriend() {
        Utils.postToastLong(ADD_FRIEND_ERROR_MSG, FriendsList.this);
    }

    private void errorAddInvalidFriend() {
        Utils.postToastLong(ADD__INVALID_FRIEND_ERROR_MSG, FriendsList.this);
    }


    // TODO
    // Delete friend
    // Handle share/unshare of exercise w/friends (multiple listeners on list view buttons)


    // ========   helpers to start new activity  ===================

    private void goToHome() {
        startActivity(new Intent(FriendsList.this, MainActivity.class));
    }

    private void goToLightExercise() {
        startActivity(new Intent(FriendsList.this, LightExercises.class));
    }

    private void goToSleepGoal() {
        startActivity(new Intent(FriendsList.this, WakeupSleepGoal.class));
    }

    private void goToLeaderboard() {
        startActivity(new Intent(FriendsList.this, Leaderboard.class));
    }
}
