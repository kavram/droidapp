package com.upmile.android.listeners;

import com.upmile.android.MerchantRegistrationActivity;
import com.upmile.util.BizCategory;
import com.upmile.util.BizSubCategory;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;

public class CategorySpinnerListener extends Activity implements OnItemSelectedListener {

	private MerchantRegistrationActivity merRegActivity;
	private BizSubCategory selectSubCategory;
	
	public CategorySpinnerListener(MerchantRegistrationActivity merRegActivity) {
		this.merRegActivity = merRegActivity;
		selectSubCategory = new BizSubCategory();
		selectSubCategory.setId("0");
		selectSubCategory.setName("Select Subcategory");
		merRegActivity.setSelSubCategory(selectSubCategory);
		SubCategorySpinnerListener subListener = new SubCategorySpinnerListener(merRegActivity);
		merRegActivity.getSubCategoriesSpinner().setOnItemSelectedListener(subListener);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		BizCategory bc = (BizCategory) parent.getItemAtPosition(pos);
		merRegActivity.setSelCategory(bc);
		ArrayAdapter<BizSubCategory> adapter = new ArrayAdapter<BizSubCategory>(merRegActivity, android.R.layout.simple_spinner_item);
		adapter.add(selectSubCategory);
		for(BizSubCategory bsc : bc.getSubCategories()){
			adapter.add(bsc);
		}
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		merRegActivity.getSubCategoriesSpinner().setAdapter(adapter);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
