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
		//reloadUser();
		getMenuInflater().inflate(R.menu.activity_my_settings, menu);
		JSONObject user = FileHelper.getData(FileHelper.USER, getApplicationContext());
		try {
			if(user.get("biz_owner").equals("1")){
				menu.removeItem(R.id.merchant_registration_check);
				menu.removeItem(R.id.my_deals);
				menu.removeItem(R.id.new_deal);
				menu.removeItem(R.id.my_merchant_profile);
				//menu.add(R.id.merchant_group, R.id.merchant_registration, 1, R.string.merchant_registration);
			}else if(user.get("biz_owner").equals("3")){
				menu.removeItem(R.id.my_deals);
				menu.removeItem(R.id.new_deal);
				menu.removeItem(R.id.my_merchant_profile);
				menu.removeItem(R.id.merchant_registration);
				//menu.add(R.id.merchant_group, R.id.merchant_registration_check, 1, R.string.merchant_reg_check);
			}else if(user.get("biz_owner").equals("2")){
				menu.removeItem(R.id.merchant_registration);
				menu.removeItem(R.id.merchant_registration_check);
				//menu.add(R.id.merchant_group, R.id.my_merchant_profile, 3, R.string.my_merchant_profile);
				//menu.add(R.id.merchant_group, R.id.my_deals, 2, R.string.my_deals);
				//menu.add(R.id.merchant_group, R.id.new_deal, 1, R.string.new_deal);
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
			JSONObject user = FileHelper.getData(FileHelper.USER, getApplicationContext());
			HttpPostHelper hpe = new HttpPostHelper(11);
			hpe.addParameter("id", user.getString("id"));
			JSONArray ar = hpe.post();
			if(ar != null && ar.length() > 0){
				user = ar.getJSONObject(0);
				FileHelper.saveData(FileHelper.USER, user.toString(), getApplicationContext());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
