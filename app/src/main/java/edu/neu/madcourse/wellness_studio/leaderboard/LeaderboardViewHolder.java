package edu.neu.madcourse.wellness_studio.leaderboard;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.wellness_studio.R;
import edu.neu.madcourse.wellness_studio.friendsList.Friend;

public class LeaderboardViewHolder  extends RecyclerView.ViewHolder {

    TextView friendUsername;
    TextView rank;

    LeaderboardViewHolder(View v) {
        super(v);
        friendUsername = v.findViewById(R.id.leaderboardUsername);
        rank = v.findViewById(R.id.weeklyCount);
    }

    public void bindThisData(Friend friend) {
        friendUsername.setText(friend.getFriendUsername());
        rank.setText(friend.getFriendRank());
    }
}
