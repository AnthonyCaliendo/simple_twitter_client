package com.anthonycaliendo.twitterclient.ui.timeline;

import com.anthonycaliendo.twitterclient.R;
import com.anthonycaliendo.twitterclient.Tweet;
import com.anthonycaliendo.twitterclient.service.TwitterClient;
import com.anthonycaliendo.twitterclient.ui.TweetListFragment;

/**
 * Defines the fragment behavior for the home view/page/timeline.
 */
public class HomeFragment extends TweetListFragment {
    @Override
    protected void callTwitter(TwitterClient client, Tweet oldestTweet, TwitterClient.TweetsResponseHandler tweetsResponseHandler) {
        client.fetchHomeTimeline(oldestTweet, tweetsResponseHandler);
    }

    @Override
    protected int getFragmentResourceId() {
        return R.layout.fragment_home;
    }
}
