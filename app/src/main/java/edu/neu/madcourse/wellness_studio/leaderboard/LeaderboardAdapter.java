package edu.neu.madcourse.wellness_studio.leaderboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.neu.madcourse.wellness_studio.R;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardViewHolder> {

    private final List<String> friendEmailList;
    private final List<String> friendWeeklyCount;
    private final Context context;
    String TAG = "ADAPTER_TEST";

    public LeaderboardAdapter(Context context, List<String> friendEmailList, List<String> friendWeeklyCount) {
        this.context = context;
        this.friendEmailList = friendEmailList;
        this.friendWeeklyCount = friendWeeklyCount;
    }


    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LeaderboardViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_leaderboard_card_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        holder.bindThisData(friendEmailList.get(position), friendWeeklyCount.get(position));
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "getItemCount: " + friendsList.size());
        return friendEmailList.size();
        // return friendWeeklyCount.size();
    }
}
