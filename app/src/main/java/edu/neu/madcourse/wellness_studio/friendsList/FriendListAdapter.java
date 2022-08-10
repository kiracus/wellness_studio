package edu.neu.madcourse.wellness_studio.friendsList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;
import java.util.Objects;

import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.utils.UserService;
import edu.neu.madcourse.wellness_studio.utils.Utils;
import localDatabase.AppDatabase;
import localDatabase.userInfo.User;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder> {

    List<String> friendsList;
    List<String> imgList;
    private final Context context;
    ToggleButton exerciseShareButton;


    public FriendListAdapter(Context context, List<String> friendsList, List<String> imgList) {
        this.context = context;
        this.friendsList = friendsList;
        this.imgList = imgList;
        setHasStableIds(true);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public FriendListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendListViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_friends_list_card_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FriendListViewHolder holder, int position) {
        holder.bindThisData(friendsList.get(position), imgList.get(position));
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class FriendListViewHolder extends RecyclerView.ViewHolder {

        TextView friendEmail;
        ImageView friendImg;
        String friendId = "";


        FriendListViewHolder(View v) {
            super(v);
            friendEmail = v.findViewById(R.id.friendListEmail);
            friendImg = v.findViewById(R.id.personIcon);
            exerciseShareButton = v.findViewById(R.id.exerciseShareButton);

              // When clicking on whole row, give user option to delete friend or cancel
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d("demo", "OnClick + " + getAdapterPosition());
//                }
//            });

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
                                            refresh();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
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
            exerciseShareButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

                    Log.d("demo", "OnClick Share/Unshare for user" + getAdapterPosition());
                    Log.d("demo", "OnClick results in user email " + friendsList.get(pos));

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
                                        Utils.postToastLong("You have stopped sharing your exercise goal with this user.", context);
                                        refresh();
                                    } else {
                                        dbUserRef.child(user.userId)
                                                .child("friends")
                                                .child(friendId)
                                                .child("shareTo").setValue(true);
                                        dbUserRef.child(friendId)
                                                .child("friends")
                                                .child(user.userId)
                                                .child("shareFrom").setValue(true);
                                        Utils.postToastLong("You started sharing your exercise goal with this user.", context);
                                        refresh();
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

        public void bindThisData(String thePersonToBind, String imgUriString) {
            Picasso.get().setLoggingEnabled(true);
            Uri imgUri = Uri.parse(imgUriString);
            Picasso.get().load(imgUri).transform(new CircleTransform()).resize(190,190).into(friendImg);
            friendEmail.setText(thePersonToBind);
        }

        public void refresh() {
            ((Activity)context).finish();
            ((Activity)context).overridePendingTransition(0,0);
            ((Activity)context).startActivity(((Activity)context).getIntent());
            ((Activity)context).overridePendingTransition(0,0);
        }


    }

    // Circular transformation from source: https://gist.github.com/aprock/6213395
    public static class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap img) {
            int size = Math.min(img.getWidth(), img.getHeight());

            int x = (img.getWidth() - size) / 2;
            int y = (img.getHeight() - size) / 2;

            Bitmap squareBitmap = Bitmap.createBitmap(img, x, y, size, size);
            if (squareBitmap != img) {
                img.recycle();
            }
            Bitmap bm = Bitmap.createBitmap(size, size, img.getConfig());
            Paint paint = new Paint();
            Canvas canvas = new Canvas(bm);
            BitmapShader shader = new BitmapShader(squareBitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squareBitmap.recycle();
            return bm;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

}
