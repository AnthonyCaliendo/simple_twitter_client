package com.anthonycaliendo.twitterclient.ui.hometimeline;

import android.content.Context;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anthonycaliendo.twitterclient.R;
import com.anthonycaliendo.twitterclient.Tweet;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

public class TweetAdapter extends ArrayAdapter<Tweet> {

    private static class ViewHolder {
        ImageView profileImage;
        TextView name;
        TextView username;
        TextView body;
        TextView tweetedAt;
        ImageView image;
    }

    public TweetAdapter(final Context context, final List<Tweet> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Tweet item = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_tweet, parent, false);
            viewHolder.profileImage = (ImageView)convertView.findViewById(R.id.tweet_profile_image);
            viewHolder.name = (TextView)convertView.findViewById(R.id.tweet_name);
            viewHolder.username = (TextView)convertView.findViewById(R.id.tweet_username);
            viewHolder.body = (TextView)convertView.findViewById(R.id.tweet_body);
            viewHolder.tweetedAt = (TextView)convertView.findViewById(R.id.tweet_tweeted_at);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.tweet_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.body.setText(item.body);
        viewHolder.name.setText(item.getTweeterName());
        viewHolder.username.setText(item.getTweeterUsername());
        viewHolder.tweetedAt.setText(convertToRelativeTimestamp(item.tweetedAt));

        if (item.getTweeterProfileImageUrl() == null) {
            viewHolder.profileImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.default_profile_normal));
        } else {
            Picasso.with(getContext())
                    .load(Uri.parse(item.getTweeterProfileImageUrl()))
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .transform(new RoundedRectangleTransformation(10, 1))
                    .into(viewHolder.profileImage);
        }

        if (item.imageUrl != null) {
            Picasso.with(getContext())
                    .load(Uri.parse(item.imageUrl))
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .transform(new RoundedRectangleTransformation(10, 1))
                    .into(viewHolder.image);
        } else {
            viewHolder.image.setImageDrawable(null);
        }

        return convertView;
    }

    private String convertToRelativeTimestamp(final Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return DateUtils.getRelativeTimeSpanString(
                calendar.getTimeInMillis(),
                System.currentTimeMillis(),DateUtils.SECOND_IN_MILLIS).toString();
    }
}
