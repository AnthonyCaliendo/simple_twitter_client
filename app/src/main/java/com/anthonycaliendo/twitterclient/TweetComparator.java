package com.anthonycaliendo.twitterclient;

import java.util.Comparator;

/**
 * Compares two tweets, sorting in reverse id order (i.e. largest id on top).
 */
public class TweetComparator implements Comparator<Tweet> {
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