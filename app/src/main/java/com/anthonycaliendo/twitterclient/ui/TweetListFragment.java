package com.anthonycaliendo.twitterclient.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycaliendo.twitterclient.R;
import com.anthonycaliendo.twitterclient.RestApplication;
import com.anthonycaliendo.twitterclient.Tweet;
import com.anthonycaliendo.twitterclient.TweetComparator;
import com.anthonycaliendo.twitterclient.service.TwitterClient;

import java.util.ArrayList;
import java.util.List;

import static com.anthonycaliendo.twitterclient.Instrumentation.debug;

/**
 * Abstract {@link Fragment} which provides base functionality for timeline based fragments.
 */
public abstract class TweetListFragment extends Fragment {
    /**
     * The maximum number of tweets we can load.
     */
    private static final int MAX_TWEETS = 800;

    private SwipeRefreshLayout  timeline;
    private TweetAdapter tweetAdapter;
    private TwitterClient       twitterClient;
    private Tweet               oldestTweet;
    private Tweet               postedTweet;
    private boolean             loadingData;
    private boolean             noResults = false;
    private NetworkProgressable progressIndicator;
    private TextView            noTweets;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        twitterClient = RestApplication.getRestClient();
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        tweetAdapter      = new TweetAdapter(context, new ArrayList<Tweet>());
        // throw a ClassCastException here if the Activity doesn't implement the interface
        progressIndicator = (NetworkProgressable) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view          = inflater.inflate(getFragmentResourceId(), container, false);
        final ListView tweetList = (ListView) view.findViewById(R.id.timeline_tweets);
        noTweets                 = (TextView) view.findViewById(R.id.timeline_no_tweets);

        tweetList.setAdapter(tweetAdapter);
        tweetList.setOnScrollListener(new EndlessScrollListener(1) {
            @Override
            public boolean onLoadMore(final int page, final int totalItemsCount) {
                if (loadingData || noResults) {
                    return true;
                }

                if (totalItemsCount >= MAX_TWEETS) {
                    return false;
                }

                loadingData = true;
                debug(this, "handler=onScrollListener page=" + page + " totalItemsCount=" + totalItemsCount + " ");
                loadTimeline(false, false);

                return true;
            }
        });

        timeline = (SwipeRefreshLayout) view.findViewById(R.id.timeline);
        timeline.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                debug(this, "handler=setOnRefreshListener");
                loadTimeline(true, true);
            }
        });

        timeline.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        loadTimeline(true, false);

        return view;
    }

    /**
     * Loads the timeline into the view fragment. If this is for a new search, certain UI components
     * are triggered.
     *
     * @param newSearch
     *      true if this is for a new search, and false otherwise.
     * @param refreshSearch
     */
    private void loadTimeline(final boolean newSearch, final boolean refreshSearch) {
        if (progressIndicator != null) {
            progressIndicator.onBeforeCall(newSearch && !refreshSearch);
        }

        if (newSearch) {
            oldestTweet = null;
            postedTweet = null;
            noResults   = false;
        }

        callTwitter(twitterClient, oldestTweet, new TwitterClient.TweetsResponseHandler() {
            @Override
            public void onSuccess(final List<Tweet> tweets) {
                debug(this, "method=loadTimeline handler=onSuccess newSearch=" + newSearch + " refreshSearch=" + refreshSearch + " tweetsSize=" + tweets.size());
                if (progressIndicator != null) {
                    progressIndicator.onCallSucceeded(newSearch);
                }

                final boolean isEmptyResult = isEmptyResult(tweets);
                if (isEmptyResult) {
                    noResults = true;
                }

                if (newSearch || refreshSearch) {
                    tweetAdapter.clear();
                } else if (postedTweet != null && tweets.contains(postedTweet)) {
                    tweetAdapter.remove(postedTweet);
                    postedTweet = null;
                }

                if (!isEmptyResult) {
                    tweetAdapter.addAll(tweets);
                    tweetAdapter.sort(new TweetComparator());
                }

                if (tweetAdapter.getCount() > 0) {
                    oldestTweet = tweetAdapter.getItem(tweetAdapter.getCount() - 1);
                    noTweets.setVisibility(View.GONE);
                } else {
                    noTweets.setVisibility(View.VISIBLE);
                }
                loadingData = false;
            }

            @Override
            public void onFail() {
                debug(this, "method=loadTimeline handler=OnFail");
                if (progressIndicator != null) {
                    progressIndicator.onCallFailed(newSearch);
                }
                Toast.makeText(
                        getContext(),
                        getString(R.string.timeline_unable_to_load_tweets_error_message),
                        Toast.LENGTH_LONG
                ).show();
                loadingData = false;
            }
        });

        timeline.setRefreshing(false);
    }

    /**
     * Inspects the results and determines if the results represent "empty" results.
     * @param tweets
     *      the result tweets
     * @return
     *      true if the results are empty, and false otherwise
     */
    private boolean isEmptyResult(List<Tweet> tweets) {
        if (tweets.isEmpty()) {
                return true;
        }

        /*
        twitter has a bug where it will return a single result which contains max_id from the
        previous query if only one result exists.
        this works around this bug by checking for a single result which matches the previous oldest tweet.
        */
        if (tweets.size() > 1) {
            return false;
        }

        final Tweet tweet = tweets.get(0);
        return tweet.twitterId == oldestTweet.twitterId;
    }

    /**
     * Invoke the twitter API call.
     * @param client
     *      the twitter client to use
     * @param oldestTweet
     *      the oldest tweet we have loaded so far
     * @param tweetsResponseHandler
     *      the responsehandler to pass into the call
     */
    abstract protected void callTwitter(TwitterClient client, Tweet oldestTweet, TwitterClient.TweetsResponseHandler tweetsResponseHandler);

    /**
     * Returns the fragment resource id to use to use as the fragment.
     * @return
     *      the fragment resource id to use
     */
    abstract protected int getFragmentResourceId();
}
