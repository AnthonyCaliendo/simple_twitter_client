package com.anthonycaliendo.twitterclient.ui.timeline;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.anthonycaliendo.twitterclient.R;
import com.anthonycaliendo.twitterclient.Tweet;
import com.anthonycaliendo.twitterclient.User;
import com.anthonycaliendo.twitterclient.service.TwitterClient;
import com.anthonycaliendo.twitterclient.ui.RoundedRectangleTransformation;
import com.anthonycaliendo.twitterclient.ui.TweetListActivity;
import com.anthonycaliendo.twitterclient.ui.profile.ProfileActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import static com.anthonycaliendo.twitterclient.Instrumentation.debug;

/**
 * Main activity which allows the user to view timelines.
 */
public class TimelineActivity extends TweetListActivity {

    /**
     * The maximum character size for a single tweet.
     */
    public static final int MAX_TWEET_SIZE = 140;

    private User user;

    /** in child **/
    private Tweet              postedTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timelines);
        setSupportActionBar((Toolbar) findViewById(R.id.timeline_toolbar));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.timeline_tabs_content);
        final TimelineFragmentPagerAdapter adapter = new TimelineFragmentPagerAdapter(getSupportFragmentManager(), TimelineActivity.this);
        viewPager.setAdapter(adapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.timeline_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.timeline, menu);

        setMenuIconFromUserProfileImage(this, menu.getItem(1), 5);

        return true;
    }

    private void setMenuIconFromUserProfileImage(final TimelineActivity activity, final MenuItem menuItem, final int remainingRetries) {
        twitterClient.fetchCurrentUser(new TwitterClient.UserResponseHandler() {
            @Override
            public void onSuccess(User user) {
                debug(activity, "method=setMenuIconFromUserProfileImage handler=onSuccess");
                activity.user = user;
                Picasso.with(activity)
                        .load(Uri.parse(user.profileImageUrl))
                        .transform(new RoundedRectangleTransformation(10, 1))
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                debug(activity, "method=setMenuIconFromUserProfileImage handler=onBitmapLoaded");
                                menuItem.setIcon(new BitmapDrawable(bitmap));
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                debug(activity, "method=setMenuIconFromUserProfileImage handler=onBitmapFailed remainingRetries=" + remainingRetries);
                                if (remainingRetries >= 0) {
                                    setMenuIconFromUserProfileImage(activity, menuItem, remainingRetries - 1);
                                }
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            }

            @Override
            public void onFail() {
                debug(this, "method=setMenuIconFromUserProfileImage handler=onFail");
                Toast.makeText(
                        activity,
                        getString(R.string.timeline_unable_to_load_user_profile),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.hometimeline_menu_new_tweet) {
            showNewTweetDialog();
            return true;
        } else if (id == R.id.hometimeline_menu_user_profile) {
            displayUserProfile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayUserProfile() {
        final Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(User.PARCELABLE_KEY, user);
        startActivity(intent);
    }
    private void showNewTweetDialog() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.new_tweet_title)
                .customView(R.layout.fragment_new_tweet, true)
                .positiveText(R.string.new_tweet_save)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(final MaterialDialog dialog, final DialogAction which) {
                        final EditText body = (EditText) dialog.getCustomView().findViewById(R.id.new_tweet_body);
                        postTweet(body.getText().toString());
                    }
                })
                .negativeText(R.string.new_tweet_cancel)
                .autoDismiss(true)
                .backgroundColorRes(R.color.colorPrimaryDark)
                .titleColor(Color.WHITE)
                .positiveColor(Color.WHITE)
                .negativeColor(Color.WHITE)
                .iconRes(R.drawable.ic_new_tweet)
                .show();

        final View newTweetView = dialog.getCustomView();
        final MDButton submitButton = dialog.getActionButton(DialogAction.POSITIVE);
        final TextView remainingCharacterCounter = (TextView) newTweetView.findViewById(R.id.new_tweet_remaining_character_count);
        final EditText bodyText = (EditText) newTweetView.findViewById(R.id.new_tweet_body);

        updateTweetCounter("", remainingCharacterCounter, bodyText, submitButton);

        bodyText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                updateTweetCounter(s, remainingCharacterCounter, bodyText, submitButton);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTweetCounter(s, remainingCharacterCounter, bodyText, submitButton);
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateTweetCounter(s, remainingCharacterCounter, bodyText, submitButton);
            }
        });
    }

    private void updateTweetCounter(final CharSequence input, final TextView remainingCharacterCounter, final EditText bodyText, final MDButton submitButton) {
        if (input.length() >= MAX_TWEET_SIZE) {
            submitButton.setEnabled(false);
        } else {
            submitButton.setEnabled(true);
        }
        int remainingCharacters = MAX_TWEET_SIZE - input.length();
        if (remainingCharacters <= 0) {
            bodyText.setTextColor(Color.RED);
        } else {
            bodyText.setTextColor(Color.BLACK);
        }
        remainingCharacterCounter.setText(Integer.toString(remainingCharacters));
    }

    private void postTweet(final String body) {
        debug(this, "method=postTweet body=" + body);
        final TimelineActivity activity = this;
        postedTweet = new Tweet();
        postedTweet.body = body;

        twitterClient.tweet(postedTweet, new TwitterClient.TweetsResponseHandler() {
            @Override
            public void onSuccess(final List<Tweet> tweets) {
                debug(this, "method=postTweet handler=onSuccess");
                postedTweet = tweets.get(0);
//                tweetAdapter.add(postedTweet);
//                tweetAdapter.sort(new TweetComparator());
            }

            @Override
            public void onFail() {
                debug(this, "method=postTweet handler=OnFail");
                Toast.makeText(
                        activity,
                        getString(R.string.new_tweet_unable_to_post_tweet_error_message),
                        Toast.LENGTH_LONG
                ).show();
//                loadingData = false;
            }
        });
    }

}
