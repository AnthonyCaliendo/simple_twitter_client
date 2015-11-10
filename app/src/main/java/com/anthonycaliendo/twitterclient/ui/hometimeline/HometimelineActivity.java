package com.anthonycaliendo.twitterclient.ui.hometimeline;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.anthonycaliendo.twitterclient.R;
import com.anthonycaliendo.twitterclient.RestApplication;
import com.anthonycaliendo.twitterclient.Tweet;
import com.anthonycaliendo.twitterclient.service.TwitterClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.anthonycaliendo.twitterclient.Instrumentation.debug;

/**
 * Displays the homeline.
 */
public class HometimelineActivity extends AppCompatActivity {

    /**
     * The maximum number of tweets we can load.
     */
    private static final int MAX_TWEETS = 800;
    public static final int MAX_TWEET_SIZE = 140;

    private SwipeRefreshLayout hometimeline;
    private TweetAdapter       tweetAdapter;
    private TwitterClient      twitterClient;
    private Tweet oldestTweet;
    private Tweet              postedTweet;
    private boolean            loadingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hometimeline);
        setSupportActionBar((Toolbar) findViewById(R.id.hometimeline_toolbar));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        tweetAdapter  = new TweetAdapter(this, new ArrayList<Tweet>());
        twitterClient = RestApplication.getRestClient();

        final ListView tweetList = (ListView) findViewById(R.id.hometimeline_tweets);
        tweetList.setAdapter(tweetAdapter);
        tweetList.setOnScrollListener(new EndlessScrollListener(1) {
            @Override
            public boolean onLoadMore(final int page, final int totalItemsCount) {
                if (loadingData) {
                    return true;
                }

                if (totalItemsCount >= MAX_TWEETS) {
                    return false;
                }

                loadingData = true;
                debug(this, "handler=onScrollListener page=" + page + " totalItemsCount=" + totalItemsCount + " ");
                loadHometimeline(false);

                return true;
            }
        });

        hometimeline = (SwipeRefreshLayout) findViewById(R.id.hometimeline);
        hometimeline.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadHometimeline(true);
            }
        });

        hometimeline.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        loadHometimeline(true);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.hometimeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.hometimeline_menu_new_tweet) {
            showNewTweetDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
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


    private void loadHometimeline(final boolean newSearch) {
        final HometimelineActivity activity = this;

        final TextView networkIssueText = (TextView) findViewById(R.id.hometimeline_network_error_message);
        networkIssueText.setVisibility(TextView.GONE);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.hometimeline_loading_results);
        if (newSearch) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.bringToFront();
        }

        if (newSearch) {
            oldestTweet = null;
            postedTweet = null;
        }
        twitterClient.fetchHomeTimeline(oldestTweet, new TwitterClient.ResponseHandler() {
            @Override
            public void onSuccess(final List<Tweet> tweets) {
                debug(this, "method=loadHometimeline handler=onSuccess");
                progressBar.setVisibility(ProgressBar.GONE);

                if (newSearch) {
                    tweetAdapter.clear();
                } else if (postedTweet != null && tweets.contains(postedTweet)) {
                    tweetAdapter.remove(postedTweet);
                    postedTweet = null;
                }

                tweetAdapter.addAll(tweets);
                tweetAdapter.sort(new TweetComparator());
                if (tweetAdapter.getCount() > 0) {
                    oldestTweet = tweetAdapter.getItem(tweetAdapter.getCount() - 1);
                }
                loadingData = false;
            }

            @Override
            public void onFail() {
                debug(this, "method=loadHometimeline handler=OnFail");
                progressBar.setVisibility(ProgressBar.GONE);
                if (newSearch) {
                    networkIssueText.setVisibility(TextView.VISIBLE);
                }
                Toast.makeText(
                        activity,
                        getString(R.string.hometimeline_unable_to_load_tweets_error_message),
                        Toast.LENGTH_LONG
                ).show();
                loadingData = false;
            }
        });
        hometimeline.setRefreshing(false);
    }

    private void postTweet(final String body) {
        debug(this, "method=postTweet body=" + body);
        final HometimelineActivity activity = this;
        postedTweet = new Tweet();
        postedTweet.body = body;

        twitterClient.tweet(postedTweet, new TwitterClient.ResponseHandler() {
            @Override
            public void onSuccess(final List<Tweet> tweets) {
                debug(this, "method=postTweet handler=onSuccess");
                postedTweet = tweets.get(0);
                tweetAdapter.add(postedTweet);
                tweetAdapter.sort(new TweetComparator());
            }

            @Override
            public void onFail() {
                debug(this, "method=postTweet handler=OnFail");
                Toast.makeText(
                        activity,
                        getString(R.string.hometimeline_unable_to_post_tweet_error_message),
                        Toast.LENGTH_LONG
                ).show();
                loadingData = false;
            }
        });
    }

    private static class TweetComparator implements Comparator<Tweet> {
        @Override
        public int compare(Tweet lhs, Tweet rhs) {
            if (lhs == rhs) {
                return 0;
            } else if (lhs == null) {
                return -1;
            } else if (rhs == null) {
                return 1;
            } else {
                return rhs.compareTo(lhs); // reverse order
            }
        }
    }
}
