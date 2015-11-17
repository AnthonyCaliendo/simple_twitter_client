package com.anthonycaliendo.twitterclient.service;

import android.support.annotation.Nullable;

import com.anthonycaliendo.twitterclient.Tweet;
import com.anthonycaliendo.twitterclient.User;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.anthonycaliendo.twitterclient.Instrumentation.debug;

/**
 * Parses json responses into model objects.
 */
public class ResponseParser {

    /**
     * The date format used by twitter for dates.
     */
    private static final String[] TWITTER_DATE_FORMAT = new String[]{"EEE MMM dd HH:mm:ss ZZZZZ yyyy"};

    /**
     * Parses all tweets in the json array.  Will return an empty List if unable to parse any tweets.
     * Invalid tweets will be ignored and not included in the list.
     * This method will return an empty list if null is passed as an argument.
     *
     * @param jsonArray
     *      the json array of tweets
     * @return
     *      the list of tweets
     */
    List<Tweet> parseTweetList(final JSONArray jsonArray) {
        final ArrayList<Tweet> tweets = new ArrayList<>();

        if (jsonArray == null) {
            return tweets;
        }

        for (int tweetIndex = 0; tweetIndex < jsonArray.length(); tweetIndex++) {
            final Tweet tweet = parseTweet(jsonArray.optJSONObject(tweetIndex));

            if (tweet != null) {
                tweets.add(tweet);
            }
        }

        return tweets;
    }

    /**
     * Parse an individual tweet. Will return null if null is passed or if the tweet cannot be parsed.
     *
     * @param jsonObject
     *      the json object to parse the tweet from
     * @return
     *      the parsed tweet, or null if unable to parse
     */
    public Tweet parseTweet(final JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;

        }

        final Tweet tweet = new Tweet();

        tweet.body      = jsonObject.optString("text");
        tweet.tweeter   = parseUser(jsonObject.optJSONObject("user"));
        tweet.tweetedAt = parseCalendar(jsonObject.optString("created_at"));
        tweet.twitterId = jsonObject.optLong("id");
        tweet.imageUrl  = extractImageUrl(jsonObject);


        return tweet;
    }

    @Nullable
    private String extractImageUrl(JSONObject jsonObject) {
        String imageUrl = null;

        final JSONObject extendedEntities = jsonObject.optJSONObject("extended_entities");
        if (extendedEntities != null) {
            final JSONArray mediaArray = extendedEntities.optJSONArray("media");
            if (mediaArray != null) {
                for (int mediaIndex = 0; mediaIndex < mediaArray.length(); mediaIndex++) {
                    final JSONObject mediaObject = mediaArray.optJSONObject(mediaIndex);
                    if (mediaObject == null) {
                        continue;
                    }

                    if (mediaObject.optString("type").equals("photo")) {
                        imageUrl = mediaObject.optString("media_url");
                    }
                }
            }
        }

        return imageUrl;
    }

    /**
     * Parses a timestamp and returns a {@link Calendar} object. Will return null if null is passed
     * or unable to parse.
     *
     * @param timestamp
     *      the timestamp
     * @return
     *      the parsed calendar, or null if unable to parse
     */
    private Calendar parseCalendar(final String timestamp) {
        if (timestamp == null) {
            return null;
        }

        final Date date;
        try {
            date = DateUtils.parseDate(timestamp, TWITTER_DATE_FORMAT);
        } catch (final DateParseException e) {
            return null;
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Parses a {@link User} from the json object. Will return null if null is passed or unable to parse.
     * @param jsonObject
     *      the json object representing the user data
     * @return
     *      the parsed user, or null if unable to parse
     */
    public User parseUser(final JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        final User user = new User();

        user.twitterId                 = jsonObject.optLong("id");
        user.username                  = jsonObject.optString("screen_name");
        user.name                      = jsonObject.optString("name");
        user.profileImageUrl           = jsonObject.optString("profile_image_url");
        user.description               = jsonObject.optString("description");
        user.followersCount            = jsonObject.optInt("followers_count");
        user.followingCount            = jsonObject.optInt("friends_count");
        user.profileBackgroundImageUrl = jsonObject.optString("profile_background_image_url");
        user.tweetCount                = jsonObject.optInt("statuses_count");

        if (user.username != null) {
            user.username = "@" + user.username;
        }

        if (user.profileImageUrl != null) {
            user.profileImageUrl = user.profileImageUrl.replace("_normal.", "_bigger.");
        }

        return user;
    }
}
