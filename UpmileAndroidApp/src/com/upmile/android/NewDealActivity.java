package com.upmile.android;

import java.io.File;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.upmile.android.listeners.StateSpinnerListener;
import com.upmile.util.FileHelper;
import com.upmile.util.HttpFormSubmitHelper;
import com.upmile.util.HttpPostHelper;
import com.upmile.util.State;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;

@SuppressLint("SimpleDateFormat")
public class NewDealActivity extends Activity {
	
	private NewDealActivity thisActivity = null;
	private LoadBizDetailsTask loadBizTask;
	private SubmitNewDealTask submitNewDealTask;
	private String dealName;
	private String dealDescr;
	private Date dealExp;
	private String regPrice;
	private String dealPrice;
	private String locationName;
	private String street;
	private String city;
	private State state;
	private String zipcode;
	private String phone;
	private JSONObject bizDetails;

	private EditText locationNameView;
	private EditText streetView;
	private EditText cityView;
	private Spinner stateView;
	private EditText zipcodeView;
	
	private EditText dealNameView;
	private EditText dealDescrView;
	private EditText dealExpTimeView;
	private EditText dealExpDateView;
	private EditText regPriceView;
	private EditText dealPriceView;
	private EditText phoneView;
	private View dealFormView;
	private TextView newDealStatusView;
	private Button submitButton;
	private Button okButton;
	private Button cancelButton;
	private ArrayList<String> dealPics = null; 
	
	static final int GET_NEWDEAL_PHOTO_REQUEST = 1;
	
	public static final String DEAL_PICS_LIST = "deal_pics_list";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        setContentView(R.layout.activity_new_deal);
    	dealFormView = findViewById(R.id.newdeal_form);
    	newDealStatusView = (TextView)findViewById(R.id.newdeal_textview);
        loadBizTask = new LoadBizDetailsTask();
    	showProgress(true);
    	loadBizTask.execute((Void) null);

    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GET_NEWDEAL_PHOTO_REQUEST) {
	        if (resultCode == RESULT_OK) {
	        	Bundle bndl = data.getExtras();
	        	TextView pic1View = (TextView)findViewById(R.id.newdeal_picsView);
	        	pic1View.setVisibility(View.VISIBLE);
	        	dealPics = bndl.getStringArrayList(NewDealPhotoActivity.PICS_ARRAY);
	        	pic1View.setText(dealPics.size() + " deal picture(s) were added.");
	        	
	        }
		}
	}    
    
	public void setState(State state) {
		this.state = state;
	}
    
    
    private void completeOnCreate(){
		try {
	    	dealNameView = (EditText)findViewById(R.id.deal_name);
	    	dealDescrView = (EditText)findViewById(R.id.newdeal_descr);
	    	dealExpTimeView = (EditText)findViewById(R.id.newdeal_exptime);
	    	dealExpDateView = (EditText)findViewById(R.id.newdeal_expdate);
	    	regPriceView = (EditText)findViewById(R.id.newdeal_oldprice);
	    	dealPriceView = (EditText)findViewById(R.id.newdeal_newprice);
			submitButton = (Button)findViewById(R.id.newdeal_submit_button);
			cancelButton = (Button)findViewById(R.id.newdeal_cancel_button);
	    	locationNameView = (EditText)findViewById(R.id.location_name);
	    	streetView = (EditText)findViewById(R.id.street);
	    	cityView = (EditText)findViewById(R.id.city);
			stateView = (Spinner) findViewById(R.id.state);
			phoneView = (EditText)findViewById(R.id.newdeal_phone);
			ArrayAdapter<State> arStates = new ArrayAdapter<State>(thisActivity, android.R.layout.simple_spinner_item);
			for(State st : State.getStates())
				arStates.add(st);
			arStates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			stateView.setAdapter(arStates);
			StateSpinnerListener ssl = new StateSpinnerListener(
					new StateSpinnerListener.OnStateSelectedHandler() {
						@Override
						public void handleStateSelectedEvent(State state) {
							setState(state);
						}
					}
				);

			submitButton.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							submitNewDeal();
						}
					});
			cancelButton.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							thisActivity.finish();
							Intent intent = new Intent(thisActivity, MySettingsActivity.class);
							startActivity(intent);
						}
					});
			
			stateView.setOnItemSelectedListener(ssl);
	    	zipcodeView = (EditText)findViewById(R.id.zipcode);
	    	
			phoneView.setText(bizDetails.getJSONObject("nodes").getString("phone"));
			if(bizDetails.getJSONObject("nodes").getJSONArray("locations") != null){
				JSONObject loc = bizDetails.getJSONObject("nodes").getJSONArray("locations").getJSONObject(0);
				locationNameView.setText(loc.getString("name"));
				streetView.setText(loc.getString("street"));
				cityView.setText(loc.getString("city"));
				int pos = 0;
				for(State st : State.getStates()){
					if(st.getLabel().equals(loc.getString("state"))){
						stateView.setSelection(pos);
					}
					pos++;
				}
				zipcodeView.setText(loc.getString("zipcode"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_new_deal, menu);
        return true;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.newdeal_addpics_menu:
            	startAddNewDealPicsActivity();
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }

	private void startAddNewDealPicsActivity() {
		Intent intent = new Intent(this, NewDealPhotoActivity.class);
		intent.putStringArrayListExtra(DEAL_PICS_LIST, dealPics);
		startActivityForResult(intent, GET_NEWDEAL_PHOTO_REQUEST);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			newDealStatusView.setVisibility(View.VISIBLE);
			newDealStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							newDealStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			dealFormView.setVisibility(View.VISIBLE);
			dealFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							dealFormView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					});
		} else {
			newDealStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			dealFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
    
	private void submitNewDeal() {
		locationNameView.setError(null);
		streetView.setError(null);
		dealNameView.setError(null);
		streetView.setError(null);
		cityView.setError(null);
		zipcodeView.setError(null);
		dealDescrView.setError(null);
		dealExpTimeView.setError(null);
		dealExpDateView.setError(null);
		regPriceView.setError(null);
		dealPriceView.setError(null);
		phoneView.setError(null);
		locationName = locationNameView.getText().toString();
		street = streetView.getText().toString();
		city = cityView.getText().toString();
		zipcode = zipcodeView.getText().toString();
		dealName = dealNameView.getText().toString();
		dealDescr = dealDescrView.getText().toString();
		phone = phoneView.getText().toString();
		String dealExpTimeTemp = dealExpTimeView.getText().toString();
		String dealExpDateTemp = dealExpDateView.getText().toString();
		String regPriceTemp = regPriceView.getText().toString();
		String dealPriceTemp = dealPriceView.getText().toString();
		Time dealExprTime = null;
		java.sql.Date dealExprDate = null;
		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(dealName)) {
			dealNameView.setError(getString(R.string.error_field_required));
			focusView = dealNameView;
			cancel = true;
		}else if(TextUtils.isEmpty(dealExpTimeTemp)){
			dealExpTimeView.setError(getString(R.string.error_field_required));
			focusView = dealExpTimeView;
			cancel = true;
		}else if(TextUtils.isEmpty(dealExpDateTemp)){
			dealExpDateView.setError(getString(R.string.error_field_required));
			focusView = dealExpDateView;
			cancel = true;
		}else if(TextUtils.isEmpty(regPriceTemp) ){
			regPriceView.setError(getString(R.string.error_field_required));
			focusView = regPriceView;
			cancel = true;
		}else if (TextUtils.isEmpty(dealPriceTemp)) {
			dealPriceView.setError(getString(R.string.error_field_required));
			focusView = dealPriceView;
			cancel = true;
		}else if (TextUtils.isEmpty(locationName)) {
			locationNameView.setError(getString(R.string.error_field_required));
			focusView = locationNameView;
			cancel = true;
		}else if (TextUtils.isEmpty(street)) {
			streetView.setError(getString(R.string.error_field_required));
			focusView = streetView;
			cancel = true;
		}else if (TextUtils.isEmpty(phone)) {
			phoneView.setError(getString(R.string.error_field_required));
			focusView = phoneView;
			cancel = true;
		} else if (TextUtils.isEmpty(city)) {
			cityView.setError(getString(R.string.error_field_required));
			focusView = cityView;
			cancel = true;
		}//else if(TextUtils.isEmpty(state)){
		 //	stateView.setError(getString(R.string.error_field_required));
		//	focusView = stateView;
		//	cancel = true; }
		else if (TextUtils.isEmpty(zipcode)) {
			zipcodeView.setError(getString(R.string.error_field_required));
			focusView = zipcodeView;
			cancel = true;
		}
		dealExprTime = getExprTime();
		if(dealExprTime == null){
			focusView = dealExpTimeView;
			cancel = true;
		}
		
		if(!cancel){
			dealExprDate = getExprDate();
			if(dealExprDate == null){
				focusView = dealExpDateView;
				cancel = true;
			}else{
				Calendar cal = Calendar.getInstance();
				cal.clear();
				cal.setTimeInMillis(dealExprDate.getTime());
				cal.setTime(dealExprTime);
				dealExp = new Date(cal.getTimeInMillis());
			}
		}
		if (cancel)
			focusView.requestFocus();
		else{
			regPrice = regPriceTemp;
			dealPrice = regPrice;
			showProgress(true);
			submitNewDealTask = new SubmitNewDealTask();
			submitNewDealTask.execute((Void) null);
		}
		
		
	}	
	
	private java.sql.Date getExprDate(){
		java.sql.Date exprDate = null;
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
		try {
			Date date = df.parse(dealExpDateView.getText().toString());
			exprDate = new java.sql.Date(date.getTime());
		} catch (ParseException e) {
			dealExpTimeView.setError(getString(R.string.error_newdeal_exprdate_notvalid));
		}
		return exprDate;
	}
	
	private Time getExprTime(){
		SimpleDateFormat df = new SimpleDateFormat("h:mma");
		Time exprTime = null;
		try {
			Date date = df.parse(dealExpTimeView.getText().toString());
			exprTime = new Time(date.getTime());
		} catch (ParseException e) {
			dealExpTimeView.setError(getString(R.string.error_newdeal_exprtime_notvalid));
		}
		return exprTime;
	}

	public class SubmitNewDealTask extends AsyncTask<Void, Void, Boolean> {
		
		@SuppressLint("NewApi")
		@Override
		protected Boolean doInBackground(Void... params) {
			boolean ret = false;
			try {
				JSONObject user = FileHelper.getUser(getApplicationContext());
				HttpFormSubmitHelper http = new HttpFormSubmitHelper("/newdeal");
				if(dealPics != null && dealPics.size() > 0){
					Integer name = Integer.valueOf(1);
					for(String path : dealPics){
						File file = new File(path);
						http.addFileParameter(name.toString(), file);
						name++;
					}
				}
				http.addStringParameter("user_id", user.getString("id"));
				http.addStringParameter("uuid", user.getString("uuid"));
				http.addStringParameter("biz_id", bizDetails.getJSONObject("nodes").getString("id"));
				http.addStringParameter("category_id", bizDetails.getJSONObject("nodes").getString("category_id"));
				http.addStringParameter("subcategory_id", bizDetails.getJSONObject("nodes").getString("subcategory_id"));
				http.addStringParameter("name", dealName);
				if(dealDescr != null && !dealDescr.isEmpty())
					http.addStringParameter("description", dealDescr);
				SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy h:mm a");
				http.addStringParameter("expiration", df.format(dealExp));
				http.addStringParameter("regular_price", regPrice.toString());
				http.addStringParameter("deal_price", dealPrice.toString());
				http.addStringParameter("address_name", locationName);
				http.addStringParameter("street", street);
				http.addStringParameter("city", city);
				http.addStringParameter("state", state.getLabel());
				http.addStringParameter("zipcode", zipcode);
				http.addStringParameter("phone", phone);
				JSONObject loc = bizDetails.getJSONObject("nodes").getJSONArray("locations").getJSONObject(0);
				http.addStringParameter("latitude", loc.getString("latitude"));
				http.addStringParameter("longitude", loc.getString("longitude"));
				HttpResponse resp = http.post();
				HttpEntity he = resp.getEntity();
				char[] buffer = new char[(int) he.getContentLength()];
				InputStreamReader isr = new InputStreamReader(he.getContent());
				isr.read(buffer);
				String str = new String(buffer);
				if(str.indexOf("submit_status=\"success\"") != -1){
						user.put("biz_owner", "3");
						FileHelper.saveUser(user.toString(), getApplicationContext());
						ret = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ret;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			loadBizTask = null;
			showProgress(false);
			if(success){
				completeOnCreate();
			}
		}

		@Override
		protected void onCancelled() {
			loadBizTask = null;
			showProgress(false);
		}
	}
	
	
	public class LoadBizDetailsTask extends AsyncTask<Void, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Void... params) {
			boolean status = false;
			try {
				JSONObject user = FileHelper.getUser(getApplicationContext());
				HttpPostHelper hpe = new HttpPostHelper(23);
				hpe.addParameter("owner_id", user.getString("id"));
				hpe.addParameter("uuid", user.getString("uuid"));
				JSONArray ar = hpe.post();
				if(ar.length() > 0){
					bizDetails = ar.getJSONObject(0);
					status = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return status;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			loadBizTask = null;
			showProgress(false);
			if(success){
				completeOnCreate();
			}
		}

		@Override
		protected void onCancelled() {
			loadBizTask = null;
			showProgress(false);
		}
	}
    
    
    
    
}
