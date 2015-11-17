package com.anthonycaliendo.twitterclient.ui.profile;

import android.os.Bundle;

import com.anthonycaliendo.twitterclient.R;
import com.anthonycaliendo.twitterclient.Tweet;
import com.anthonycaliendo.twitterclient.User;
import com.anthonycaliendo.twitterclient.service.TwitterClient;
import com.anthonycaliendo.twitterclient.ui.TweetListFragment;

/**
 * Exended {@link TweetListFragment} which shows user tweets.
 */
public class TweetsFragment extends TweetListFragment {

    public static final String TWITTER_USER_ID = "twitterUserId";

    private User user;

    public static TweetsFragment newInstance(final User user) {
        final TweetsFragment tweetsFragment = new TweetsFragment();
        final Bundle arguments              = new Bundle();

        arguments.putLong(TWITTER_USER_ID, user.twitterId);
        tweetsFragment.setArguments(arguments);

        return tweetsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user           = new User();
        user.twitterId = getArguments().getLong(TWITTER_USER_ID);
    }

    @Override
    protected void callTwitter(TwitterClient client, Tweet oldestTweet, TwitterClient.TweetsResponseHandler tweetsResponseHandler) {
        client.fetchUserTimeline(user, oldestTweet, tweetsResponseHandler);
    }

    @Override
    protected int getFragmentResourceId() {
        return R.layout.fragment_profile_tweets;
    }
}
