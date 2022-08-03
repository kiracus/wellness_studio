package edu.neu.madcourse.wellness_studio.friendsList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.wellness_studio.R;

public class FriendListViewHolder extends RecyclerView.ViewHolder {

        //ImageView personIcon;
        TextView friendEmail;

        FriendListViewHolder(View v) {
            super(v);
            //personIcon = v.findViewById(R.id.personIcon);
            friendEmail = v.findViewById(R.id.friendListEmail);
        }

    public void bindThisData(String thePersonToBind) {
        friendEmail.setText(thePersonToBind);
    }

}
