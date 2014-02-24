package com.upmile.android;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
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
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;

public class MerchantRegistrationActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<BizCategory>> {
	
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
	private String mFName;
	private String mLName;

	// UI references.
	private EditText mFNameView;
	private EditText mLNameView;
	
	
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
	//private Map<String, BizCategory> categories = null;
	private Button regButton;
	private Button okButton;
	private Button cancelButton;
	ArrayAdapter<BizCategory> adapterCategories = null;
	
	//LoadCategoriesTask loadCategoriesTask = null;
	SubmitMerchantRegistrationTask mRegisterTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant_registration);
		thisActivity = this;
		View regForm = findViewById(R.id.merchant_register_form);
		regForm.setVisibility(View.INVISIBLE);
		stateSpinner = (Spinner) findViewById(R.id.reg_state);
		ArrayAdapter<State> arStates = new ArrayAdapter<State>(thisActivity, android.R.layout.simple_spinner_item);
		for(State st : State.getStates())
			arStates.add(st);
		arStates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stateSpinner.setAdapter(arStates);
		StateSpinnerListener ssl = new StateSpinnerListener(
			new StateSpinnerListener.OnStateSelectedHandler() {
				@Override
				public void handleStateSelectedEvent(State state) {
					setState(state);
				}
			}
		);
		stateSpinner.setOnItemSelectedListener(ssl);
		setupActionBar();
		setSelCategory(null);
		setSelSubCategory(null);
		mRegisterFormView = findViewById(R.id.merchant_register_form);
		mRegisterStatusView = (TextView)findViewById(R.id.register_status_textview);
		categoriesSpinner = (Spinner) findViewById(R.id.category);
		subCategoriesSpinner = (Spinner) findViewById(R.id.subcategory);
		mRegisterFormView.setVisibility(View.GONE);
		adapterCategories = new ArrayAdapter<BizCategory>(thisActivity, android.R.layout.simple_spinner_item);
		adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categoriesSpinner.setAdapter(adapterCategories);
		CategorySpinnerListener csl = new CategorySpinnerListener(thisActivity);
		categoriesSpinner.setOnItemSelectedListener(csl);
		getLoaderManager().initLoader(0, null, this);
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
		stateSpinner = (Spinner) findViewById(R.id.reg_state);
		mFNameView = (EditText) findViewById(R.id.owner_fname);
		mLNameView = (EditText) findViewById(R.id.owner_lname);
		
		
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
		okButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						thisActivity.finish();
						Intent intent = new Intent(thisActivity, MainActivity.class);
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
		mFName = mFNameView.getText().toString();
		mLName = mLNameView.getText().toString();
		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(mFName)) {
			mFNameView.setError(getString(R.string.error_field_required));
			focusView = mFNameView;
			cancel = true;
		}else if (TextUtils.isEmpty(mLName)) {
			mLNameView.setError(getString(R.string.error_field_required));
			focusView = mLNameView;
			cancel = true;
		}else if (TextUtils.isEmpty(merchantName)) {
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
		private boolean emailExist = false;
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			boolean ret = false;
			try {
				JSONObject user = insertUser();
				if(user == null){
					emailExist = true;
					return false;
				}
				HttpPostHelper post = new HttpPostHelper(28);
				post.addParameter("zipcode", zipcode);
				JSONArray rez = post.post();
				if(rez.length() > 0){
					JSONObject coor = rez.getJSONObject(0);
					post = new HttpPostHelper(19);
					JSONArray locs = new JSONArray();
					JSONObject loc = new JSONObject();
					JSONObject flds = new JSONObject();
					flds.accumulate("owner_id", user.getString("id"));
					flds.accumulate("uuid", user.getString("uuid"));
					flds.accumulate("name", merchantName);
					flds.accumulate("category_id", selCategory.getId());
					flds.accumulate("subcategory_id", selSubCategory.getId());
					flds.accumulate("phone", phone);
					flds.accumulate("email", email);
					loc.accumulate("street", street);
					loc.accumulate("city", city);
					loc.accumulate("state", state.getLabel());
					loc.accumulate("zipcode", zipcode);
					loc.accumulate("latitude", coor.getJSONObject("nodes").getString("latitude"));
					loc.accumulate("longitude", coor.getJSONObject("nodes").getString("longitude"));
					locs.put(loc);
					flds.put("locations", locs);
					post.addFields(flds);
					rez = post.post();
					if(rez.length() > 0){
						JSONObject biz = rez.getJSONObject(0).getJSONObject("nodes");
						FileHelper.saveData(FileHelper.BIZ, biz.toString(), getApplicationContext());
						ret = true;
					}
				}
			} catch (Exception e) {
				
			}
			return ret;
		}

	private void showBizInsertConfirmation(){
		mFNameView.setVisibility(View.GONE);
		mLNameView.setVisibility(View.GONE);
		merchantNameView.setVisibility(View.GONE);
		phoneView.setVisibility(View.GONE);
		emailView.setVisibility(View.GONE);
		streetView.setVisibility(View.GONE);
		cityView.setVisibility(View.GONE);
		stateSpinner.setVisibility(View.GONE);
		categoriesSpinner.setVisibility(View.GONE);
		subCategoriesSpinner.setVisibility(View.GONE);
		zipcodeView.setVisibility(View.GONE);
		regButton.setVisibility(View.GONE);
		cancelButton.setVisibility(View.GONE);
		okButton.setVisibility(View.VISIBLE);
		mRegisterStatusView.setVisibility(View.VISIBLE);
		mRegisterStatusView.setText("Your merchant account has been created. We sent you an email to confirm your registration.");
		
	}
		
	private JSONObject insertUser(){	
		JSONObject user = null;
		try {
			//check email for dupe
			JSONObject flds = new JSONObject();
			HttpPostHelper hpe = new HttpPostHelper(12);
			hpe.addParameter("email", email);
			JSONArray resp = hpe.post();
			if(resp.length() > 0){
				return null;
			}
			//insert user
			hpe = new HttpPostHelper(8);
			flds.accumulate("first_name", mFName);
			flds.accumulate("last_name", mLName);
			flds.accumulate("email", email);
			hpe.addFields(flds);
			JSONArray ar = hpe.post();
			if(ar.length() > 0){
				JSONObject rep = ar.getJSONObject(0);
				if(rep.has("nodes") && rep.getJSONObject("nodes").has("id")){
					user = rep.getJSONObject("nodes");
					FileHelper.saveData(FileHelper.USER, user.toString(), getApplicationContext());
				}
			}
		} catch (Exception e) {
		}
		return user;
	}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			mRegisterTask = null;
			showProgress(false);
			if(success) {
				mRegisterStatusView.setVisibility(View.VISIBLE);
				mRegisterStatusView.setText(getString(R.string.merchant_register_success));
				mFNameView.setVisibility(View.GONE);
				mLNameView.setVisibility(View.GONE);
				merchantNameView.setVisibility(View.GONE);
				phoneView.setVisibility(View.GONE);
				emailView.setVisibility(View.GONE);
				streetView.setVisibility(View.GONE);
				cityView.setVisibility(View.GONE);
				stateSpinner.setVisibility(View.GONE);
				categoriesSpinner.setVisibility(View.GONE);
				subCategoriesSpinner.setVisibility(View.GONE);
				zipcodeView.setVisibility(View.GONE);
				regButton.setVisibility(View.GONE);
				cancelButton.setVisibility(View.GONE);
				okButton.setVisibility(View.VISIBLE);
			} else {
				if(emailExist){
					emailView.setError(getString(R.string.register_error_email_exist));
					emailView.requestFocus();
				}
			}
		}
		
		
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		if(mRegisterTask != null)
			mRegisterTask.cancel(true);
	}

	
	@Override
	public Loader<List<BizCategory>> onCreateLoader(int arg0, Bundle arg1) {
		return new BizCategoriesLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<List<BizCategory>> arg0, List<BizCategory> list) {
		adapterCategories.addAll(list);
		
	}

	@Override
	public void onLoaderReset(Loader<List<BizCategory>> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
