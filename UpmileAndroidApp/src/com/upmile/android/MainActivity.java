package com.upmile.android;

import java.util.List;

import org.json.JSONObject;

import com.upmile.util.FileHelper;
import android.app.LoaderManager;
import android.os.Bundle;
import android.content.Intent;
import android.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.support.v4.app.FragmentActivity;
//import com.google.android.gms.maps.SupportMapFragment;


public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<DealBean>> {

	
	private ListView dealsListView;
	private DealsAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dealsListView = (ListView) findViewById(R.id.deals_list);
		adapter = new DealsAdapter(this.getApplicationContext());
		dealsListView.setAdapter(adapter);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	protected void onStart(){
		super.onStart();
		//JSONObject us = FileHelper.getUser(getApplicationContext());
		//if(us != null){
			
		//}
		
		//getDeals();
	}
	
	//@Override
	//protected void onResume(){
	//	super.onResume();
	//}
	
	//@Override
	//protected void onStop(){
	//	super.onStop();
	//}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		String status = null;
		try{
			JSONObject biz = FileHelper.getData(FileHelper.BIZ, getApplicationContext());
			if(biz != null)
				status = biz.getString("status");
		}catch(Exception e){
			
		}
		if(status != null){
			if(status.equals("0")){
				menu.removeItem(R.id.merchant_registration);
				menu.removeItem(R.id.new_deal);
				menu.removeItem(R.id.my_deals);
				menu.removeItem(R.id.my_merchant_profile);
			}else if(status.equals("1")){
				menu.removeItem(R.id.merchant_reg_check);
				menu.removeItem(R.id.merchant_registration);
			}else{
				menu.removeItem(R.id.new_deal);
				menu.removeItem(R.id.my_deals);
				menu.removeItem(R.id.my_merchant_profile);
				menu.removeItem(R.id.merchant_reg_check);
			}
		}else{
			menu.removeItem(R.id.new_deal);
			menu.removeItem(R.id.my_deals);
			menu.removeItem(R.id.my_merchant_profile);
			menu.removeItem(R.id.merchant_reg_check);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.merchant_registration:
	            startMerchantRegistrationActivity();
	            return true;
	        case R.id.merchant_reg_check:
	            startMerchantRegCheckActivity();
	            return true;
	            
			default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private void startMerchantRegCheckActivity() {
		Intent intent = new Intent(this, CheckMerchantRegistrationActivity.class);
		startActivity(intent);
	}

	private void startMerchantRegistrationActivity() {
		Intent intent = new Intent(this, MerchantRegistrationActivity.class);
		startActivity(intent);
	}
	
	@Override
	public Loader<List<DealBean>> onCreateLoader(int arg0, Bundle arg1) {
		
		return new DealsLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<List<DealBean>> loader, List<DealBean> data) {
		adapter.addAll(data);
		
	}

	@Override
	public void onLoaderReset(Loader<List<DealBean>> arg0) {
		// TODO Auto-generated method stub
		
	}	
}
