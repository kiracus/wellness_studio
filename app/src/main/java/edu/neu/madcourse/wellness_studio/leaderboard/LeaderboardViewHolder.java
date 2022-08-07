package edu.neu.madcourse.wellness_studio.leaderboard;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.neu.madcourse.wellness_studio.R;

public class LeaderboardViewHolder extends RecyclerView.ViewHolder {

    public TextView friendEmailTV;
    public TextView friendRecord;
    public ProgressBar weeklyProgress;

    public LeaderboardViewHolder(@NonNull View itemView) {
        super(itemView);
        this.friendEmailTV = itemView.findViewById(R.id.leaderboardEmail);
        this.friendRecord = itemView.findViewById(R.id.weeklyCount);
        this.weeklyProgress = itemView.findViewById(R.id.weeklyProgressBar);
    }

    public void bindThisData(String email, String count) {
        friendEmailTV.setText(email);
        friendRecord.setText(count);
        weeklyProgress.setProgress(Integer.parseInt(count));
    }
}
