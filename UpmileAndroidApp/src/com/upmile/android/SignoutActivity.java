package com.upmile.android;

import com.upmile.util.FileHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;

public class SignoutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signout);
		findViewById(R.id.signout).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						handleSignout();
					}
				});
		findViewById(R.id.cancel_signout).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						startMainActivity();
					}
				});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_signout, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void handleSignout() {
		FileHelper.removeUser(getApplicationContext());
		startMainActivity();
	}

	private void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}	
	

}
