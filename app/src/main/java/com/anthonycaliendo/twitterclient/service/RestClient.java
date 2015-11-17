package com.anthonycaliendo.twitterclient.service;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
class RestClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL                     = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY            = "lqxs8Al7MAUob4gkpFN70gsoi";//"57fdgdfh345195e071f9a761d763ca0";
	public static final String REST_CONSUMER_SECRET         = "Y3eHgKzxkWcrMBeo4b2rwdDukGtBVCKQejgOk34i7E2Z3a2JMZ";//"d657sdsg34435435";
	public static final String REST_CALLBACK_URL            = "x-oauthflow-twitter://anthonycaliendotweets.com";//oauth://anthonycaliendotweets";

	public RestClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

}