package com.upmile.android;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.upmile.util.FileHelper;
import com.upmile.util.HttpPostHelper;
import android.app.LoaderManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.FragmentActivity;
//import com.google.android.gms.maps.SupportMapFragment;


public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<String>> {

	
	private GetDealsTask getDealsTask;
	private ListView dealsListView;
	private ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dealsListView = (ListView) findViewById(R.id.deals_list);
		adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1);
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
	
	
	private void getDeals(){
		getDealsTask = new GetDealsTask();
		getDealsTask.execute();
		
	}
	
	public class GetDealsTask extends AsyncTask<Void, Void, Boolean> {
		private JSONObject ret = null;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				HttpPostHelper hpe = new HttpPostHelper(63);
				hpe.addSpParameter("lat", 37.76515);
				hpe.addSpParameter("lon", -122.481);
				hpe.addSpParameter("dist", 45);
				JSONArray resp = hpe.post();
				ret = resp.getJSONObject(1);
				return true;
			} catch (Exception e) {
				return false;
			}
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		//JSONObject user = FileHelper.getUser(getApplicationContext());
		//if(user != null){
		//	menu.removeItem(R.id.signin);
		//	menu.removeItem(R.id.menu_register);
		//}else{
			menu.removeItem(R.id.mysettings);
			menu.removeItem(R.id.signout);
		//}
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

	@Override
	public Loader<List<String>> onCreateLoader(int arg0, Bundle arg1) {
		
		return new DealsLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
		adapter.addAll(data);
		
	}

	@Override
	public void onLoaderReset(Loader<List<String>> arg0) {
		// TODO Auto-generated method stub
		
	}	
}
