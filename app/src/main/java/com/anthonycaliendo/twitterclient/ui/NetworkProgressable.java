package com.anthonycaliendo.twitterclient.ui;

/**
 * Defines an interface for handling network call progress.
 */
public interface NetworkProgressable {

    /**
     * Invoked before the call is made.
     *
     * @param initialRequest
     *      true if this call is for an initial loading of data, and false otherwise.
     */
    void onBeforeCall(boolean initialRequest);

    /**
     * Invoked when a call fails.
     *
     * @param initialRequest
     *      true if this call is for an initial loading of data, and false otherwise.
     */
    void onCallFailed(boolean initialRequest);

    /**
     * Invoked when a call succeeds.
     *
     * @param initialRequest
     *      true if this call is for an initial loading of data, and false otherwise.
     */
    void onCallSucceeded(boolean initialRequest);
}
