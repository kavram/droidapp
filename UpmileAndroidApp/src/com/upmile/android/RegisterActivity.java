package com.upmile.android;

import org.json.JSONArray;
import org.json.JSONObject;

import com.upmile.util.FileHelper;
import com.upmile.util.HttpPostHelper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class RegisterActivity extends Activity {

	private UserRegisterTask mRegisterTask = null;

	// Values at the time of the registration attempt.
	private String mFName;
	private String mLName;
	private String mEmail;
	private String mPassword;
	private String mConfirmPassword;

	// UI references.
	private EditText mFNameView;
	private EditText mLNameView;
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mPasswordConfirmView;
	private View mRegisterFormView;
	private View mRegisterStatusView;
	private TextView mRegStatusView;
	private Button regButton;
	private Button okButton;
	private RegisterActivity ra;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		setupActionBar();
		mFNameView = (EditText) findViewById(R.id.register_fname);
		mLNameView = (EditText) findViewById(R.id.register_lname);
		
		mEmailView = (EditText) findViewById(R.id.register_email);
		mEmailView.setText(mEmail);
		mPasswordConfirmView = (EditText) findViewById(R.id.register_confirm_password);
		
		mPasswordView = (EditText) findViewById(R.id.register_password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
						if (id == R.id.register_button || id == EditorInfo.IME_NULL) {
							attemptRegistration();
							return true;
						}
						return false;
					}
				});

		regButton = (Button)findViewById(R.id.register_button);
		okButton = (Button)findViewById(R.id.ok);
		mRegisterFormView = findViewById(R.id.register_form);
		mRegisterStatusView = findViewById(R.id.register_status);
		mRegStatusView = (TextView) findViewById(R.id.regstatus);
		mRegStatusView.setVisibility(View.GONE);
		ra = this;
		
		regButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegistration();
					}
				});

		okButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						ra.finish();
						Intent intent = new Intent(ra, MainActivity.class);
						startActivity(intent);

					}
				});
		okButton.setVisibility(View.GONE);
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_register, menu);
		return true;
	}

	public void attemptRegistration() {
		if (mRegisterTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);
		mFNameView.setError(null);
		mLNameView.setError(null);
		mPasswordConfirmView.setError(null);
		// Store values at the time of the login attempt.
		mFName = mFNameView.getText().toString();
		mLName = mLNameView.getText().toString();
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mConfirmPassword = mPasswordConfirmView.getText().toString();
		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(mFName)) {
			mFNameView.setError(getString(R.string.error_field_required));
			focusView = mFNameView;
			cancel = true;
		} else if (TextUtils.isEmpty(mLName)) {
			mLNameView.setError(getString(R.string.error_field_required));
			focusView = mLNameView;
			cancel = true;
		} else if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}else if (TextUtils.isEmpty(mConfirmPassword)) {
			mPasswordConfirmView.setError(getString(R.string.error_field_required));
			focusView = mPasswordConfirmView;
			cancel = true;
		}else if (!mPassword.equals(mConfirmPassword)) {
			mPasswordConfirmView.setError(getString(R.string.register_error_password_dont_match));
			focusView = mPasswordConfirmView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
//			mRegisterStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mRegisterTask = new UserRegisterTask();
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

	/**
	 * Represents an asynchronous registration task.
	 */
	public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
		private JSONObject ret = null;
		private boolean emailExist = false;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				//check email for dupe
				JSONObject flds = new JSONObject();
				HttpPostHelper hpe = new HttpPostHelper(12);
				hpe.addParameter("email", mEmail);
				JSONArray resp = hpe.post();
				if(resp.length() > 0){
					emailExist = true;
					return false;
				}
				hpe = new HttpPostHelper(8);
				flds.accumulate("first_name", mFName);
				flds.accumulate("last_name", mLName);
				flds.accumulate("email", mEmail);
				flds.accumulate("password", mPassword);
				hpe.addFields(flds);
				JSONArray ar = hpe.post();
				if(ar.length() > 0){
					JSONObject rep = ar.getJSONObject(0);
					if(rep.has("nodes") && rep.getJSONObject("nodes").has("id")){
						ret = rep;
						return true;
					}else
						return false;
				}else
					return false;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mRegisterTask = null;
			showProgress(false);
			if (success) {
				mRegStatusView.setText(getString(R.string.register_success));
				try {
					FileHelper.saveUser(ret.getJSONObject("nodes").toString(), getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
				}
				hideFormFields();
				okButton.requestFocus();
			} else {
				if(emailExist){
					mEmailView.setError(getString(R.string.register_error_email_exist));
					mEmailView.requestFocus();
				}else{
					hideFormFields();
					mRegStatusView.setText(getString(R.string.register_generic_error));
				}
			}
		}

		private void hideFormFields(){
			mFNameView.setVisibility(View.GONE);
			mLNameView.setVisibility(View.GONE);
			mEmailView.setVisibility(View.GONE);
			mPasswordView.setVisibility(View.GONE);
			mPasswordConfirmView.setVisibility(View.GONE);
			mRegStatusView.setVisibility(View.VISIBLE);
			regButton.setVisibility(View.GONE);
			okButton.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected void onCancelled() {
			mRegisterTask = null;
			showProgress(false);
		}
	}
}
