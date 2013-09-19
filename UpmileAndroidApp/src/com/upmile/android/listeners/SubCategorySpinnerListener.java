package com.upmile.android.listeners;

import com.upmile.android.MerchantRegistrationActivity;
import com.upmile.util.BizSubCategory;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class SubCategorySpinnerListener extends Activity implements	OnItemSelectedListener {

	private MerchantRegistrationActivity merRegActivity;
	
	public SubCategorySpinnerListener(MerchantRegistrationActivity merRegActivity){
		this.merRegActivity = merRegActivity;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		BizSubCategory bc = (BizSubCategory) parent.getItemAtPosition(pos);
		merRegActivity.setSelSubCategory(bc);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}

}
