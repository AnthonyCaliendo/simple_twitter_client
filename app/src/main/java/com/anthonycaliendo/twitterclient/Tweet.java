package com.anthonycaliendo.twitterclient;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Calendar;

/**
 * Represents a tweet.
 */
@Table(name = "tweets")
public class Tweet extends Model implements Comparable<Tweet> {

    @Column
    public long twitterId;

    @Column
    public String body;

    @Column
    public Calendar tweetedAt;

    @Column
    public User tweeter;

    @Column
    public String imageUrl;

    public Tweet() {
        super();
    }

    public String getTweeterProfileImageUrl() {
        if (tweeter == null) {
            return null;
        }

        return tweeter.profileImageUrl;
    }

    public String getTweeterUsername() {
        if (tweeter == null) {
            return null;
        }

        return tweeter.username;
    }

    public String getTweeterName() {
        if (tweeter == null) {
            return null;
        }

        return tweeter.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof Tweet)) {
            return false;
        } else {
            return twitterId == ((Tweet)obj).twitterId;
        }
    }

    @Override
    public int compareTo(final Tweet another) {
        if (this == another) {
            return 0;
        } else if (another == null) {
            return 1;
        } else {
            return Long.valueOf(twitterId).compareTo(another.twitterId);
        }
    }
}
