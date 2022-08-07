package edu.neu.madcourse.wellness_studio.friendsList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import localDatabase.AppDatabase;
import localDatabase.userInfo.User;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder> {

    List<String> friendsList;
    private final Context context;


    public FriendListAdapter(Context context, List<String> friendsList) {
        this.context = context;
        this.friendsList = friendsList;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public FriendListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendListViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_friends_list_card_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListViewHolder holder, int position) {
        holder.bindThisData(friendsList.get(position));
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class FriendListViewHolder extends RecyclerView.ViewHolder {

        TextView friendEmail;
        String friendId = "";

        FriendListViewHolder(View v) {
            super(v);
            friendEmail = v.findViewById(R.id.friendListEmail);


              // When clicking on whole row, give user option to delete friend or cancel
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d("demo", "OnClick + " + getAdapterPosition());
//                }
//            });

            // TODO make item disappear after deleting friend
            v.findViewById(R.id.deleteFriendButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.d("POSITION TO BEGIN", String.valueOf(pos));

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you wish to delete this friend?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Perform action and dismiss dialog
                            AppDatabase appDatabase = AppDatabase.getDbInstance(context);
                            User user = UserService.getCurrentUser(appDatabase);
                            assert user != null;
                            Log.d("demo", "Current user " + user.userId);

                            DatabaseReference dbRt = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference dbRt2 = FirebaseDatabase.getInstance().getReference();

                            DatabaseReference dbUser = dbRt.child("users");
                            DatabaseReference dbCurrent = dbRt2.child("users");
                            dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        try {
                                            if (ds.child("email").getValue(String.class).equals(friendsList.get(pos))) {
                                                friendId = ds.getKey();
                                                Log.d("demo", "ID OF USER TO DELETE " + friendId);
                                            }
                                        } catch (Exception e){

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            dbCurrent.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds2 : snapshot.getChildren()) {
                                        if (!Objects.equals(friendId, "") && ds2.getKey().equals(user.userId)) {
                                            Log.d("demo", "Current user " + ds2.child("friends").child(friendId).getValue());
//
                                            dbCurrent.child(user.userId)
                                                    .child("friends")
                                                    .child(friendId)
                                                    .removeValue();
                                            friendsList.remove(pos);
                                            notifyItemRemoved(pos);
                                            notifyItemRangeRemoved(pos, friendsList.size());
                                            dialog.dismiss();

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            Log.d("POSITION TO DELETE", String.valueOf(pos));

                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();

                }
            });

            // When clicking on button, share/un-share exercise with friend
            v.findViewById(R.id.exerciseShareButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

//                    Log.d("demo", "OnClick Share/Unshare for user" + getAdapterPosition());
//                    Log.d("demo", "OnClick results in user email " + friendsList.get(pos));

                    AppDatabase appDatabase = AppDatabase.getDbInstance(context);
                    User user = UserService.getCurrentUser(appDatabase);
                    assert user != null;
                    Log.d("demo", "Current user " + user.userId);

                    DatabaseReference dbRoot = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference dbRoot2 = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference dbUserRef = dbRoot.child("users");
                    DatabaseReference dbCurrentUser = dbRoot2.child("users");
                    dbUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                if (ds.child("email").getValue(String.class).equals(friendsList.get(pos))) {
                                    friendId = ds.getKey();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    dbCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds2 : snapshot.getChildren()) {
                                if (!Objects.equals(friendId, "") && ds2.getKey().equals(user.userId)) {
                                    if (ds2.child("friends").child(friendId).child("shareTo").getValue().equals(true)) {
                                        dbUserRef.child(user.userId)
                                                .child("friends")
                                                .child(friendId)
                                                .child("shareTo").setValue(false);
                                        dbUserRef.child(friendId)
                                                .child("friends")
                                                .child(user.userId)
                                                .child("shareFrom").setValue(false);
                                    } else {
                                        dbUserRef.child(user.userId)
                                                .child("friends")
                                                .child(friendId)
                                                .child("shareTo").setValue(true);
                                        dbUserRef.child(friendId)
                                                .child("friends")
                                                .child(user.userId)
                                                .child("shareFrom").setValue(true);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
        }

        public void bindThisData(String thePersonToBind) {
            friendEmail.setText(thePersonToBind);
        }
    }
}
