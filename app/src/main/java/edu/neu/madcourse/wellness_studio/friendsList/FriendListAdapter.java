package edu.neu.madcourse.wellness_studio.friendsList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.madcourse.wellness_studio.R;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListViewHolder> {

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
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }
}
