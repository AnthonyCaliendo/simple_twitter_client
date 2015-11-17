package com.anthonycaliendo.twitterclient.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.anthonycaliendo.twitterclient.R;
import com.anthonycaliendo.twitterclient.User;
import com.anthonycaliendo.twitterclient.ui.RoundedRectangleTransformation;
import com.anthonycaliendo.twitterclient.ui.TweetListActivity;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;

import static com.anthonycaliendo.twitterclient.Instrumentation.debug;

/**
 * Allows viewing of user profile data.
 */
public class ProfileActivity extends TweetListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final User user = (User) getIntent().getParcelableExtra(User.PARCELABLE_KEY);

        setContentView(R.layout.activity_profile);
        setSupportActionBar((Toolbar) findViewById(R.id.profile_toolbar));
        getSupportActionBar().setTitle(user.name);

        setupUser(user);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        finish();
        return true;
    }

    private void setupUser(final User user) {
        if (user == null) {
            debug(this, "method=setupUser user=null");
            return;
        }

        final ImageView profileBackground = (ImageView) findViewById(R.id.profile_background_image);
        final ImageView profileImage      = (ImageView) findViewById(R.id.profile_image);
        final TextView name               = (TextView) findViewById(R.id.profile_name);
        final TextView username           = (TextView) findViewById(R.id.profile_username);
        final TextView tweetCount         = (TextView) findViewById(R.id.profile_tweet_count);
        final TextView followingCount     = (TextView) findViewById(R.id.profile_following_count);
        final TextView followersCount     = (TextView) findViewById(R.id.profile_followers_count);

        name.setText(user.name);
        username.setText(user.username);

        final NumberFormat numberFormat = NumberFormat.getInstance();
        tweetCount.setText(numberFormat.format(user.tweetCount));
        followingCount.setText(numberFormat.format(user.followingCount));
        followersCount.setText(numberFormat.format(user.followersCount));

        Picasso.with(this)
                .load(Uri.parse(user.profileBackgroundImageUrl))
                .into(profileBackground);

        Picasso.with(this)
                .load(Uri.parse(user.profileImageUrl))
                .placeholder(R.drawable.loading)
                .error(R.drawable.ic_broken_image_black_24dp)
                .transform(new RoundedRectangleTransformation(10, 1))
                .into(profileImage);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profile_tweets, TweetsFragment.newInstance(user))
                .commit();
    }
}
