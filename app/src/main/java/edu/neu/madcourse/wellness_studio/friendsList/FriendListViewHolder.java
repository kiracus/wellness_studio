package edu.neu.madcourse.wellness_studio.friendsList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.neu.madcourse.wellness_studio.R;

public class FriendListViewHolder {

        ImageView personIcon;
        TextView friendName;

        FriendListViewHolder(View v) {
            personIcon = v.findViewById(R.id.personIcon);
            friendName = v.findViewById(R.id.friendListUsername);
        }

}
