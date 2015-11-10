package com.anthonycaliendo.twitterclient.service;

import android.content.Context;

import com.anthonycaliendo.twitterclient.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


import static com.anthonycaliendo.twitterclient.Instrumentation.debug;

/**
 * Interacts with the Twitter API using model objects.
 */
public class TwitterClient extends RestClient {

    /**
	 * The URL path for the home timeline.
	 */
	private static final String HOME_TIMELINE_URL = "statuses/home_timeline.json";

    /**
     * The URL path for posting a tweet.
     */
    private static final String TWEET_URL         = "statuses/update.json";

    /**
     * The param name for status (tweet body).
     */
    private static final String TWEET_BODY_PARAM_NAME = "status";

    /**
	 * Async callback to handle responses.
	 */
	public interface ResponseHandler {
		/**
		 * Callback method invoked on a successful response.
		 *
		 * @param tweets
		 *      the list of tweets being returned
		 */
		void onSuccess(List<Tweet> tweets);

		/**
		 * Callback method invoked when unable to retrieve a response for any reason.
		 */
		void onFail();
	}

	/**
	 * The response parser to use for processing responses.
	 */
	private final ResponseParser responseParser;

	public TwitterClient(final Context context) {
		super(context);
		this.responseParser = new ResponseParser();
	}

	public void fetchHomeTimeline(final Tweet oldestTweet, final ResponseHandler responseHandler) {
		final String url           = getApiUrl(HOME_TIMELINE_URL);
		final RequestParams params = new RequestParams();

        if(oldestTweet != null) {
            params.put("max_id", oldestTweet.twitterId);
        }

        debug(this, "method=fetchHometimeline url=" + url);

		try {
			getClient().get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(final int statusCode, final Header[] headers, final JSONArray response) {
                    final String responseBody;
                    if (response == null) {
                        responseBody = "null";
                    } else {
                        responseBody = response.toString();
                    }

                    debug(this, "method=fetchHomeTimeline handler=onSuccess statusCode=" + statusCode + " response=" + responseBody);
                    try {
                        responseHandler.onSuccess(responseParser.parseTweetList(response));
                    } catch (final RuntimeException e) {
                        debug(this, "method=fetchHomeTimeline handler=onSuccess", e);
                        responseHandler.onFail();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    final String responseBody;
                    if (errorResponse == null) {
                        responseBody = "null";
                    } else {
                        responseBody = errorResponse.toString();
                    }
                    debug(this, "method=fetchHomeTimeline handler=onFailure statusCode=" + statusCode + " response=" + responseBody);
                    responseHandler.onFail();
                }
            });
		} catch (final Exception e){
			debug(this, "method=fetchHomeTimeline", e);
			responseHandler.onFail();
		}
	}

    public void tweet(final Tweet tweet, final ResponseHandler responseHandler) {
        final String url           = getApiUrl(TWEET_URL);
        final RequestParams params = new RequestParams();

        params.put(TWEET_BODY_PARAM_NAME, tweet.body);

        debug(this, "method=tweet url=" + url);

        try {
            getClient().post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(final int statusCode, final Header[] headers, final JSONObject response) {
                    final String responseBody;
                    if (response == null) {
                        responseBody = "null";
                    } else {
                        responseBody = response.toString();
                    }

                    debug(this, "method=tweet handler=onSuccess statusCode=" + statusCode + " response=" + responseBody);
                    try {
                        final Tweet postedTweet = responseParser.parseTweet(response);
                        responseHandler.onSuccess(Arrays.asList(postedTweet));
                    } catch (final RuntimeException e) {
                        debug(this, "method=tweet handler=onSuccess", e);
                        responseHandler.onFail();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    final String responseBody;
                    if (errorResponse == null) {
                        responseBody = "null";
                    } else {
                        responseBody = errorResponse.toString();
                    }
                    debug(this, "method=tweet handler=onFailure statusCode=" + statusCode + " response=" + responseBody);
                    responseHandler.onFail();
                }
            });
        } catch (final Exception e){
            debug(this, "method=tweet", e);
            responseHandler.onFail();
        }
    }
}
