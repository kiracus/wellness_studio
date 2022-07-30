package edu.neu.madcourse.wellness_studio.friendsList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.neu.madcourse.wellness_studio.R;

public class FriendListAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> friendImg;
    ArrayList<String> friendUsername;

    public FriendListAdapter(Context context, ArrayList<String> friendImg, ArrayList<String> friendUsername) {
        super(context, R.layout.activity_friends_list_card_view);
        this.context = context;
        this.friendImg = friendImg;
        this.friendUsername = friendUsername;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View singleItem = convertView;
        FriendListViewHolder viewHolder = null;

        if (singleItem == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singleItem = layoutInflater.inflate(R.layout.activity_friends_list_card_view, parent,false);
            viewHolder = new FriendListViewHolder(singleItem);
            singleItem.setTag(viewHolder);
        } else {
            viewHolder = (FriendListViewHolder) singleItem.getTag();
        }

        String url = clipDoubleQuotationMarkFromUrl(friendImg.get(position));
        Picasso.get().setLoggingEnabled(true);
        Picasso.get().load(url).resize(50, 50).into(viewHolder.personIcon);
        viewHolder.friendName.setText(friendUsername.get(position));
        return singleItem;
    }

    public static String clipDoubleQuotationMarkFromUrl(String url) {
        if(url.contains("\"")) {
            url = url.replace("\"","");
        }
        return url;
    }
}
