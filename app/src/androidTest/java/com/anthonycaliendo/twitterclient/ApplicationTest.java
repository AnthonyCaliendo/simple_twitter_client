package com.anthonycaliendo.twitterclient;

import android.test.ApplicationTestCase;

public class ApplicationTest extends ApplicationTestCase<RestApplication> {
    public ApplicationTest() {
        super(RestApplication.class);
    }

    public void testApplicationCanBeCreated() {
        createApplication();
        assertNotNull("should be able to load and create application", getApplication());
    }
}