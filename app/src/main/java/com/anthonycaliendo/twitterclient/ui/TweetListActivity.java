package com.anthonycaliendo.twitterclient.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anthonycaliendo.twitterclient.R;
import com.anthonycaliendo.twitterclient.RestApplication;
import com.anthonycaliendo.twitterclient.service.TwitterClient;

/**
 * Abstract class which provides base functionality for working with timelines.
 *
 * This should really just be a custom view fragment which handles the network progress side of this,
 * which would reduce the copy and paste (future refactor).
 */
public abstract class TweetListActivity extends AppCompatActivity implements NetworkProgressable {

    protected TwitterClient twitterClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        twitterClient    = RestApplication.getRestClient();
    }

    protected TextView getNetworkIssueText() {
        return (TextView) findViewById(R.id.tweetlist_network_error_message);
    }

    protected ProgressBar getNetworkProgressBar() {
        return (ProgressBar) findViewById(R.id.tweetlist_loading_results);
    }

    @Override
    public void onBeforeCall(final boolean initialRequest) {
        getNetworkIssueText().setVisibility(View.GONE);

        if (initialRequest) {
            final ProgressBar progressBar = getNetworkProgressBar();
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.bringToFront();
        }

    }

    @Override
    public void onCallFailed(final boolean initialRequest) {
        getNetworkProgressBar().setVisibility(ProgressBar.GONE);
        if (initialRequest) {
            getNetworkIssueText().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCallSucceeded(final boolean initialRequest) {
        getNetworkIssueText().setVisibility(View.GONE);
        getNetworkProgressBar().setVisibility(ProgressBar.GONE);
    }
}
