package com.upmile.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.upmile.util.FileHelper;
import com.upmile.util.HttpPostHelper;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;

public class MySettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_settings);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		reloadUser();
		getMenuInflater().inflate(R.menu.activity_my_settings, menu);
		JSONObject user = FileHelper.getUser(getApplicationContext());
		try {
			menu.removeGroup(R.id.merchant_group);
			if(user.get("biz_owner").equals("1")){
				menu.add(Menu.NONE, R.id.merchant_registration, Menu.NONE, R.string.merchant_registration);
			}else if(user.get("biz_owner").equals("3"))
				menu.add(Menu.NONE, R.id.merchant_registration_check, Menu.NONE, R.string.merchant_registration);
			else if(user.get("biz_owner").equals("2")){
				menu.add(Menu.NONE, R.id.my_merchant_profile, Menu.NONE, R.string.my_merchant_profile);
				menu.add(Menu.NONE, R.id.my_deals, Menu.NONE, R.string.my_deals);
				menu.add(Menu.NONE, R.id.new_deal, Menu.NONE, R.string.new_deal);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.merchant_registration:
			displayMerchantRegistrationActivity();
		case R.id.merchant_registration_check:
			displayMerchantRegistrationCheckActivity();
		case R.id.new_deal:
			displayNewDealActivity();
			
		}
		return super.onOptionsItemSelected(item);
	}

	private void displayNewDealActivity() {
		Intent intent = new Intent(this, NewDealActivity.class);
		startActivity(intent);
	}

	private void displayMerchantRegistrationCheckActivity() {
		Intent intent = new Intent(this, CheckMerchantRegistrationActivity.class);
		startActivity(intent);
	}

	private void displayMerchantRegistrationActivity() {
		Intent intent = new Intent(this, MerchantRegistrationActivity.class);
		startActivity(intent);
	}

	private void reloadUser(){
		try {
			JSONObject user = FileHelper.getUser(getApplicationContext());
			HttpPostHelper hpe = new HttpPostHelper(11);
			hpe.addParameter("id", user.getString("id"));
			JSONArray ar = hpe.post();
			if(ar != null && ar.length() > 0){
				user = ar.getJSONObject(0);
				FileHelper.saveUser(user.toString(), getApplicationContext());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
