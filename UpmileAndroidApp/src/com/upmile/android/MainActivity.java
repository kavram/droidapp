package com.upmile.android;

import org.json.JSONObject;

import com.upmile.util.FileHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentActivity;
//import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends FragmentActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onStart(){
		super.onStart();
		JSONObject us = FileHelper.getUser(getApplicationContext());
		if(us != null){
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		JSONObject user = FileHelper.getUser(getApplicationContext());
		if(user != null){
			menu.removeItem(R.id.signin);
			menu.removeItem(R.id.menu_register);
		}else{
			menu.removeItem(R.id.mysettings);
			menu.removeItem(R.id.signout);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.signin:
	            startSigninActivity();
	            return true;
	        case R.id.signout:
	            startSignoutActivity();
	            return true;
			case R.id.menu_register:
				startRegisterActivity();
			case R.id.mysettings:
				startMySettingsActivity();
			default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private void startMySettingsActivity() {
		Intent intent = new Intent(this, MySettingsActivity.class);
		startActivity(intent);
	}

	private void startRegisterActivity() {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}
	
	private void startSignoutActivity() {
		Intent intent = new Intent(this, SignoutActivity.class);
		startActivity(intent);
	}

	private void startSigninActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}	
}
