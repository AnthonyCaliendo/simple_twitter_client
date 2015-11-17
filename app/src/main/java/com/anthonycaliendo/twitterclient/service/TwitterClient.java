package com.anthonycaliendo.twitterclient.service;

import android.content.Context;

import com.anthonycaliendo.twitterclient.Tweet;
import com.anthonycaliendo.twitterclient.User;
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
     * The param name for max id.
     */
    private static final String MAX_ID_PARAM_NAME = "max_id";

    /**
     * The URL path for posting a tweet.
     */
    private static final String TWEET_URL         = "statuses/update.json";

    /**
     * The param name for status (tweet body).
     */
    private static final String TWEET_BODY_PARAM_NAME = "status";

    /**
     * The URL path for the mentions timeline.
     */
    private static final String MENTIONS_TIMELINE_URL = "statuses/mentions_timeline.json";

    /**
     * The URL path for tweets for a specific user.
     */
    private static final String USER_TIMELINE_URL = "statuses/user_timeline.json";

    /**
     * The param name for user id.
     */
    private static final String USER_ID_PARAM_NAME = "user_id";

    /**
     * The URL path for the current user.
     */
    public static final String CURRENT_USER_URL = "account/verify_credentials.json";

    /**
     * Async callback to handle responses which deal with tweets.
     */
    public interface TweetsResponseHandler {
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
     * Async callback to handle responses which deal with users.
     */
    public interface UserResponseHandler {
        /**
         * Callback method invoked on a successful response.
         *
         * @param user
         *      the user being returned
         */
        void onSuccess(User user);

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

    /**
     * Fetches the home timeline.
     *
     * @param oldestTweet
     *      the oldest tweet. used for pagination with the max_id param.
     * @param tweetsResponseHandler
     *      the response handler for this request.  the returned tweets will be passed into this handler
     */
    public void fetchHomeTimeline(final Tweet oldestTweet, final TweetsResponseHandler tweetsResponseHandler) {
        final RequestParams params = new RequestParams();

        if(oldestTweet != null) {
            params.put(MAX_ID_PARAM_NAME, oldestTweet.twitterId);
        }

        getTweets(getApiUrl(HOME_TIMELINE_URL), params, tweetsResponseHandler);
    }

    /**
     * Fetches the mentions timeline.
     *
     * @param oldestTweet
     *      the oldest tweet. used for pagination with the max_id param.
     * @param tweetsResponseHandler
     *      the response handler for this request.  the returned tweets will be passed into this handler
     */
    public void fetchMentionsTimeline(final Tweet oldestTweet, final TweetsResponseHandler tweetsResponseHandler) {
        final RequestParams params = new RequestParams();

        if(oldestTweet != null) {
            params.put(MAX_ID_PARAM_NAME, oldestTweet.twitterId);
        }

        getTweets(getApiUrl(MENTIONS_TIMELINE_URL), params, tweetsResponseHandler);
    }

    /**
     * Fetches the user tweets timeline.
     *
     * @param user
     *      the user to fetch the timeline for
     * @param oldestTweet
     *      the oldest tweet. used for pagination with the max_id param.
     * @param tweetsResponseHandler
     *      the response handler for this request.  the returned tweets will be passed into this handler
     */
    public void fetchUserTimeline(final User user, final Tweet oldestTweet, final TweetsResponseHandler tweetsResponseHandler) {
        final RequestParams params = new RequestParams();

        params.put(USER_ID_PARAM_NAME, user.twitterId);
        debug(this, "userId=" + user.twitterId);

        if(oldestTweet != null) {
            params.put(MAX_ID_PARAM_NAME, oldestTweet.twitterId);
        }

        getTweets(getApiUrl(USER_TIMELINE_URL), params, tweetsResponseHandler);
    }

    public void fetchCurrentUser(final UserResponseHandler userResponseHandler) {
        getClient().get(getApiUrl(CURRENT_USER_URL), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(final int statusCode, final Header[] headers, final JSONObject response) {
                final String responseBody;
                if (response == null) {
                    responseBody = "null";
                } else {
                    responseBody = response.toString();
                }

                debug(this, "method=fetchCurrentUser handler=onSuccess statusCode=" + statusCode + " response=" + responseBody);
                try {
                    final User user = responseParser.parseUser(response);
                    userResponseHandler.onSuccess(user);
                } catch (final RuntimeException e) {
                    debug(this, "method=fetchCurrentUser handler=onSuccess", e);
                    userResponseHandler.onFail();
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
                debug(this, "method=fetchCurrentUser handler=onFailure statusCode=" + statusCode + " response=" + responseBody);
                userResponseHandler.onFail();
            }
        });
    }
    /**
     * Publishes a tweet (status update).
     *
     * @param tweet
     *      the tweet to post
     * @param tweetsResponseHandler
     *      the response handler for this request.
     */
    public void tweet(final Tweet tweet, final TweetsResponseHandler tweetsResponseHandler) {
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
                        tweetsResponseHandler.onSuccess(Arrays.asList(postedTweet));
                    } catch (final RuntimeException e) {
                        debug(this, "method=tweet handler=onSuccess", e);
                        tweetsResponseHandler.onFail();
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
                    tweetsResponseHandler.onFail();
                }
            });
        } catch (final Exception e){
            debug(this, "method=tweet", e);
            tweetsResponseHandler.onFail();
        }
    }

    /**
     * Fires a GET request.
     *  @param url
     *      the url to make a request to
     * @param params
     *      the url params for the request
     * @param tweetsResponseHandler
     *      the response handler for this request
     */
    private void getTweets(final String url, RequestParams params, final TweetsResponseHandler tweetsResponseHandler) {
        debug(this, "method=getTweets url=" + url);

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

                    debug(this, "method=getTweets url=" + url + " handler=onSuccess statusCode=" + statusCode + " response=" + responseBody);
                    try {
                        tweetsResponseHandler.onSuccess(responseParser.parseTweetList(response));
                    } catch (final RuntimeException e) {
                        debug(this, "method=fetchHomeTimeline handler=onSuccess", e);
                        tweetsResponseHandler.onFail();
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
                    debug(this, "method=getTweets url=" + url + "  handler=onFailure statusCode=" + statusCode + " response=" + responseBody);
                    tweetsResponseHandler.onFail();
                }
            });
        } catch (final Exception e){
            debug(this, "method=getTweets url=" + url, e);
            tweetsResponseHandler.onFail();
        }
    }
}
