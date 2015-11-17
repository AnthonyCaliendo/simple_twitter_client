package com.anthonycaliendo.twitterclient.ui;

import android.content.Context;
import android.content.Intent;
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
import com.anthonycaliendo.twitterclient.User;
import com.anthonycaliendo.twitterclient.ui.profile.ProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import static com.anthonycaliendo.twitterclient.Instrumentation.debug;

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
        final Tweet tweet = getItem(position);
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

            viewHolder.profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final Intent intent = new Intent(getContext(), ProfileActivity.class);
                    final Tweet tweet   =  (Tweet) view.getTag(R.layout.item_tweet);
                    if (tweet != null) {
                        intent.putExtra(User.PARCELABLE_KEY, tweet.tweeter);
                    } else {
                        debug(this, "method=getView.profileImage.onClick tweet=null");
                    }
                    getContext().startActivity(intent);
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.profileImage.setTag(R.layout.item_tweet, tweet);

        viewHolder.body.setText(tweet.body);
        viewHolder.name.setText(tweet.getTweeterName());
        viewHolder.username.setText(tweet.getTweeterUsername());
        viewHolder.tweetedAt.setText(convertToRelativeTimestamp(tweet.tweetedAt));

        if (tweet.getTweeterProfileImageUrl() == null) {
            viewHolder.profileImage.setImageDrawable(getContext().getResources().getDrawable(R.drawable.default_profile_normal));
        } else {
            Picasso.with(getContext())
                    .load(Uri.parse(tweet.getTweeterProfileImageUrl()))
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .transform(new RoundedRectangleTransformation(10, 1))
                    .into(viewHolder.profileImage);
        }

        if (tweet.imageUrl != null) {
            Picasso.with(getContext())
                    .load(Uri.parse(tweet.imageUrl))
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
