package com.upmile.android;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import com.upmile.android.listeners.CategorySpinnerListener;
import com.upmile.android.listeners.StateSpinnerListener;
import com.upmile.util.BizCategory;
import com.upmile.util.BizSubCategory;
import com.upmile.util.FileHelper;
import com.upmile.util.HttpFormSubmitHelper;
import com.upmile.util.HttpPostHelper;
import com.upmile.util.State;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;

public class MerchantRegistrationActivity extends Activity {
	
	private MerchantRegistrationActivity thisActivity = null;
	private String merchantName;
	private BizCategory selCategory;
	private BizSubCategory selSubCategory;
	private String phone;
	private String email;
	private String street;
	private String city;
	private State state;
	private String zipcode;
	
	private EditText merchantNameView;
	private EditText phoneView;
	private EditText emailView;
	private EditText streetView;
	private EditText cityView;
	private Spinner stateSpinner;
	private Spinner categoriesSpinner;
	private Spinner subCategoriesSpinner;
	private EditText zipcodeView;
	private View mRegisterFormView;
	private TextView mRegisterStatusView;
	private Map<String, BizCategory> categories = null;
	private Button regButton;
	private Button okButton;
	private Button cancelButton;
	
	LoadCategoriesTask loadCategoriesTask = null;
	SubmitMerchantRegistrationTask mRegisterTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant_registration);
		thisActivity = this;
		View regForm = findViewById(R.id.merchant_register_form);
		regForm.setVisibility(View.INVISIBLE);
		Spinner spinner = (Spinner) findViewById(R.id.state);
		ArrayAdapter<State> arStates = new ArrayAdapter<State>(thisActivity, android.R.layout.simple_spinner_item);
		for(State st : State.getStates())
			arStates.add(st);
		arStates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(arStates);
		StateSpinnerListener ssl = new StateSpinnerListener(
			new StateSpinnerListener.OnStateSelectedHandler() {
				@Override
				public void handleStateSelectedEvent(State state) {
					setState(state);
				}
			}
		);
		spinner.setOnItemSelectedListener(ssl);
		setupActionBar();
		setSelCategory(null);
		setSelSubCategory(null);
		mRegisterFormView = findViewById(R.id.merchant_register_form);
		mRegisterStatusView = (TextView)findViewById(R.id.register_status_textview);
		categoriesSpinner = (Spinner) findViewById(R.id.category);
		subCategoriesSpinner = (Spinner) findViewById(R.id.subcategory);
		mRegisterFormView.setVisibility(View.GONE);
		if(categories == null){
			showProgress(true);
			loadCategoriesTask = new LoadCategoriesTask();
			loadCategoriesTask.execute((Void) null);
			return;
		}
		completeOnCreate();
	}

	private void completeOnCreate(){
		View regForm = findViewById(R.id.merchant_register_form);
		regForm.setVisibility(View.VISIBLE);
		regButton = (Button)findViewById(R.id.bizreg_button);
		cancelButton = (Button)findViewById(R.id.bizreg_cancel_button);
		okButton = (Button)findViewById(R.id.bizreg_ok_button);
		okButton.setVisibility(View.GONE);
		merchantNameView = (EditText) findViewById(R.id.merchant_name);
		phoneView = (EditText) findViewById(R.id.phone);
		emailView = (EditText) findViewById(R.id.email);
		streetView = (EditText) findViewById(R.id.street);
		cityView = (EditText) findViewById(R.id.city);
		zipcodeView = (EditText) findViewById(R.id.zipcode);
		stateSpinner = (Spinner) findViewById(R.id.state);
		
		regButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptBizRegistration();
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
		mRegisterFormView.setVisibility(View.VISIBLE);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	
	private void attemptBizRegistration() {
		
		// Reset errors.
		merchantNameView.setError(null);
		phoneView.setError(null);
		emailView.setError(null);
		streetView.setError(null);
		cityView.setError(null);
		zipcodeView.setError(null);
		// Store values at the time of the login attempt.
		merchantName = merchantNameView.getText().toString();
		phone = phoneView.getText().toString();
		email = emailView.getText().toString();
		street = streetView.getText().toString();
		city = cityView.getText().toString();
		//state = stateView.getText().toString();
		zipcode = zipcodeView.getText().toString();
		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(merchantName)) {
			merchantNameView.setError(getString(R.string.error_field_required));
			focusView = merchantNameView;
			cancel = true;
		}else if(selCategory.getId().equals("0") ){
			categoriesSpinner.requestFocus();
			focusView = categoriesSpinner;
			cancel = true;
		}else if(selSubCategory.getId().equals("0") ){
			subCategoriesSpinner.requestFocus();
			focusView = subCategoriesSpinner;
			cancel = true;
		}else if (TextUtils.isEmpty(phone)) {
			phoneView.setError(getString(R.string.error_field_required));
			focusView = phoneView;
			cancel = true;
		}else if (TextUtils.isEmpty(email)) {
			emailView.setError(getString(R.string.error_field_required));
			focusView = emailView;
			cancel = true;
		}else if (TextUtils.isEmpty(street)) {
			streetView.setError(getString(R.string.error_field_required));
			focusView = streetView;
			cancel = true;
		} else if (TextUtils.isEmpty(city)) {
			cityView.setError(getString(R.string.error_field_required));
			focusView = cityView;
			cancel = true;
		}else if(state.getName().equals("Select State") ){
			stateSpinner.requestFocus();
			focusView = stateSpinner;
			cancel = true;
		}else if (TextUtils.isEmpty(zipcode)) {
			zipcodeView.setError(getString(R.string.error_field_required));
			focusView = zipcodeView;
			cancel = true;
		}
		
		if (cancel)
			focusView.requestFocus();
		else{
			showProgress(true);
			mRegisterTask = new SubmitMerchantRegistrationTask();
			mRegisterTask.execute((Void) null);
		}

	
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mRegisterStatusView.setVisibility(View.VISIBLE);
			mRegisterStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mRegisterStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mRegisterFormView.setVisibility(View.VISIBLE);
			mRegisterFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mRegisterStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_merchant_registration, menu);
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

	public BizCategory getSelCategory() {
		return selCategory;
	}

	public void setSelCategory(BizCategory selCategory) {
		this.selCategory = selCategory;
	}

	public void setState(State state) {
		this.state = state;
	}
	
	public BizSubCategory getSelSubCategory() {
		return selSubCategory;
	}

	public void setSelSubCategory(BizSubCategory selSubCategory) {
		this.selSubCategory = selSubCategory;
	}

	public Spinner getSubCategoriesSpinner() {
		return subCategoriesSpinner;
	}

	public class SubmitMerchantRegistrationTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {
			boolean ret = false;
			try {
				JSONObject user = FileHelper.getUser(getApplicationContext());
				HttpFormSubmitHelper http = new HttpFormSubmitHelper("/newbiz");
				http.addStringParameter("owner_id", user.getString("id"));
				http.addStringParameter("uuid", user.getString("uuid"));
				http.addStringParameter("name", merchantName);
				http.addStringParameter("category_id", selCategory.getId());
				http.addStringParameter("subcategory_id", selSubCategory.getId());
				http.addStringParameter("phone", phone);
				http.addStringParameter("email", email);
				http.addStringParameter("street", street);
				http.addStringParameter("city", city);
				http.addStringParameter("state", state.getLabel());
				http.addStringParameter("zipcode", zipcode);
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
				
			}
			return ret;
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			mRegisterTask = null;
			showProgress(false);
			if(success) {
				mRegisterStatusView.setVisibility(View.VISIBLE);
				mRegisterStatusView.setText(getString(R.string.merchant_register_success));
				merchantNameView.setVisibility(View.GONE);
				phoneView.setVisibility(View.GONE);
				emailView.setVisibility(View.GONE);
				streetView.setVisibility(View.GONE);
				cityView.setVisibility(View.GONE);
				zipcodeView.setVisibility(View.GONE);
				stateSpinner.setVisibility(View.GONE);
				categoriesSpinner.setVisibility(View.GONE);
				subCategoriesSpinner.setVisibility(View.GONE);
				regButton.setVisibility(View.GONE);
				okButton.setVisibility(View.VISIBLE);
				cancelButton.setVisibility(View.GONE);
			} else {

			}
		}
		
		
	}
	
	public class LoadCategoriesTask extends AsyncTask<Void, Void, Boolean> {
		private JSONArray jsonCategories = null;
		private JSONArray subCategories = null;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				HttpPostHelper hpe = new HttpPostHelper(4);
				subCategories = hpe.post();
				hpe = new HttpPostHelper(34);
				jsonCategories = hpe.post();
			} catch (Exception e) {
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			loadCategoriesTask = null;
			showProgress(false);
			try{
				if(success){
					BizCategory bc = new BizCategory();
					bc.setId("0");
					bc.setName("Select Category");
					selCategory = bc;
					ArrayAdapter<BizCategory> arCategories = new ArrayAdapter<BizCategory>(thisActivity, android.R.layout.simple_spinner_item);
					arCategories.add(bc);
					categories = new HashMap<String, BizCategory>();
					for(int i = 0; i < jsonCategories.length(); i ++){
						JSONObject jobj = jsonCategories.getJSONObject(i);
						BizCategory cat = new BizCategory();
						cat.setId(jobj.getJSONObject("nodes").getString("id"));
						cat.setName(jobj.getJSONObject("nodes").getString("name"));
						categories.put(cat.getId(), cat);
						arCategories.add(cat);
					}
					for(int i = 0; i < subCategories.length(); i++){
						JSONObject jobj = subCategories.getJSONObject(i);
						BizSubCategory bsc = new BizSubCategory();
						bsc.setId(jobj.getJSONObject("nodes").getString("id"));
						bsc.setName(jobj.getJSONObject("nodes").getString("name"));
						bsc.setCategoryId(jobj.getJSONObject("nodes").getString("category_id"));
						if(categories.containsKey(bsc.getCategoryId()))
							categories.get(bsc.getCategoryId()).getSubCategories().add(bsc);
					}
					
					arCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					categoriesSpinner.setAdapter(arCategories);
					CategorySpinnerListener csl = new CategorySpinnerListener(thisActivity);
					categoriesSpinner.setOnItemSelectedListener(csl);
					completeOnCreate();
			}
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		@Override
		protected void onCancelled() {
			loadCategoriesTask = null;
			showProgress(false);
		}
	}
	
	
}
