package edu.neu.madcourse.wellness_studio.friendsList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.madcourse.wellness_studio.R;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder> {

    private final List<String> friendsList;
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
//        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public static class FriendListViewHolder extends RecyclerView.ViewHolder {

        //ImageView personIcon;
        TextView friendEmail;

        FriendListViewHolder(View v) {
            super(v);
            //personIcon = v.findViewById(R.id.personIcon);
            friendEmail = v.findViewById(R.id.friendListEmail);

            // When clicking on whole row, give user option to delete friend or cancel
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("demo", "OnClick + " + getAdapterPosition());
                }
            });

            // When clicking on button, share/unshare exercise with friend
            v.findViewById(R.id.exerciseShareButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("demo", "OnClick Share/Unshare for user" + getAdapterPosition());
                }
            });
        }

        public void bindThisData(String thePersonToBind) {
            friendEmail.setText(thePersonToBind);
        }

    }
}
