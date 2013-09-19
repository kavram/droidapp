package com.upmile.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.upmile.util.FileHelper;
import com.upmile.util.HttpPostHelper;

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
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class CheckMerchantRegistrationActivity extends Activity {

	private View mCheckRegFormView;
	private TextView mStatusTextView;
	private View mStatusView;
	private Button okButton;
	private CheckMerchantRegistrationTask checkRegTask;
	private CheckMerchantRegistrationActivity thisActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_merchant_registration);
		mCheckRegFormView = findViewById(R.id.check_merchant_register_form);
		mStatusView = findViewById(R.id.check_merchant_register_status);
		mStatusTextView = (TextView)findViewById(R.id.status_textview);
		okButton = (Button)findViewById(R.id.checkreg_ok_button);
		okButton.setVisibility(View.GONE);
		thisActivity = this;
		okButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						thisActivity.finish();
						Intent intent = new Intent(thisActivity, MySettingsActivity.class);
						startActivity(intent);
					}
				});
		showProgress(true);
		checkRegTask = new CheckMerchantRegistrationTask();
		checkRegTask.execute((Void) null);
		return;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mStatusView.setVisibility(View.VISIBLE);
			mStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});
			mCheckRegFormView.setVisibility(View.VISIBLE);
			mCheckRegFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mCheckRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					});
			
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mCheckRegFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_check_merchant_registration,
				menu);
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

	public class CheckMerchantRegistrationTask extends AsyncTask<Void, Void, Boolean> {
		private JSONObject ret = null; 
		
		@Override
		protected Boolean doInBackground(Void... params) {
			boolean status = false;
			try {
				JSONObject user = FileHelper.getUser(getApplicationContext());
				HttpPostHelper hpe = new HttpPostHelper(9);
				hpe.addParameter("id", user.getString("id"));
				hpe.addParameter("uuid", user.getString("uuid"));
				JSONArray ar = hpe.post();
				if(ar.length() > 0){
					ret = ar.getJSONObject(0);
					status = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return status;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			checkRegTask = null;
			showProgress(false);
			mStatusView.setVisibility(View.GONE);
			mStatusTextView.setVisibility(View.VISIBLE);
			okButton.setVisibility(View.VISIBLE);
			if (success) {
				try {
					if(ret.getJSONObject("nodes").getString("biz_owner").equals("3")){
						mStatusTextView.setText(getString(R.string.check_merchant_reg_status_pending));
					}else if(ret.getJSONObject("nodes").getString("biz_owner").equals("2")){
						mStatusTextView.setText(getString(R.string.check_merchant_reg_status_confirmed));
						FileHelper.saveUser(ret.getJSONObject("nodes").toString(), getApplicationContext());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
			}
		}

		@Override
		protected void onCancelled() {
			checkRegTask = null;
			showProgress(false);
		}
	}
	
	
	
}
