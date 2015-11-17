package com.anthonycaliendo.twitterclient.ui.login;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.anthonycaliendo.twitterclient.R;
import com.anthonycaliendo.twitterclient.service.TwitterClient;
import com.anthonycaliendo.twitterclient.ui.timeline.TimelineActivity;
import com.codepath.oauth.OAuthLoginActionBarActivity;

import static com.anthonycaliendo.twitterclient.Instrumentation.debug;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

        setSupportActionBar((Toolbar) findViewById(R.id.login_toolbar));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_twitter_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
	}


	@Override
	protected void onResume() {
		super.onResume();
		if (isNetworkAvailable()) {
			findViewById(R.id.login_network_error_message).setVisibility(View.GONE);
		} else {
			findViewById(R.id.login_network_error_message).setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public void onLoginSuccess() {
		final Intent intent = new Intent(this, TimelineActivity.class);
		startActivity(intent);
	}

	@Override
	public void onLoginFailure(Exception e) {
		debug(this, "handler=onLoginFailure", e);
		Toast.makeText(
				this,
				getString(R.string.login_unable_to_login_error_message),
				Toast.LENGTH_LONG
		).show();
	}

	public void loginToRest(View view) {
		getClient().connect();
	}

    private Boolean isNetworkAvailable() {
        final NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}
