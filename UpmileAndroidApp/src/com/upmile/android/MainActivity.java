package com.upmile.android;

import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.upmile.util.FileHelper;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.os.Bundle;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
//import com.google.android.gms.maps.SupportMapFragment;


public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<DealBean>>, GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	private ListView dealsListView = null;
	private DealsAdapter adapter = null;
	private LocationClient mLocationClient = null;
	
	  public static class ErrorDialogFragment extends DialogFragment {
	        // Global field to contain the error dialog
		  private Dialog mDialog;
	        // Default constructor. Sets the dialog field to null
	      public ErrorDialogFragment() {
	    	  super();
	          mDialog = null;
	      }
	      // Set the dialog to display
	      public void setDialog(Dialog dialog) {
	    	  mDialog = dialog;
	      }
	      // Return a Dialog to the DialogFragment.
	      @Override
	      public Dialog onCreateDialog(Bundle savedInstanceState) {
	    	  return mDialog;
	      }
	}
	
	  private boolean servicesConnected() {
	        // Check that Google Play services is available
	        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	        // If Google Play services is available
	        if (ConnectionResult.SUCCESS == resultCode) {
	            // In debug mode, log the status
	            Log.d("Location Updates", "Google Play services is available.");
	            // Continue
	            return true;
	        // Google Play services was not available for some reason
	        } else {
	            // Get the error code
	            //int errorCode = connectionResult.getErrorCode();
	            // Get the error dialog from Google Play services
	            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
	                    resultCode,
	                    this,
	                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

	            // If Google Play services can provide an error dialog
	            if (errorDialog != null) {
	                // Create a new DialogFragment for the error dialog
	                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
	                // Set the dialog in the DialogFragment
	                errorFragment.setDialog(errorDialog);
	                // Show the error dialog in the DialogFragment
	                //errorFragment.show(getSupportFragmentManager(), "Location Updates");
	            }
	            return false;
	        }
	    }  
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLocationClient = new LocationClient(this, this, this);
		
		if(dealsListView == null)
			dealsListView = (ListView) findViewById(R.id.deals_list);
		if(adapter == null){
			adapter = new DealsAdapter(this.getApplicationContext());
			dealsListView.setAdapter(adapter);
			getLoaderManager().initLoader(0, null, this);
		}
	}

	@Override
	protected void onStart(){
		super.onStart();
		mLocationClient.connect();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
	}
	
	
	@Override
	protected void onStop(){
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	protected void onPause(){
		super.onPause();
		if(adapter != null)
			adapter.clearDeals();
	}
	
	
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

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
           // showErrorDialog(connectionResult.getErrorCode());
        }
    }
		
	

	@Override
	public void onConnected(Bundle arg0) {
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
		
	}	
}
