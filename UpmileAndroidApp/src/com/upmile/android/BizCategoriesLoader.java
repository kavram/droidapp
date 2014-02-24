package com.upmile.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.upmile.android.listeners.CategorySpinnerListener;
import com.upmile.util.BizCategory;
import com.upmile.util.BizSubCategory;
import com.upmile.util.HttpPostHelper;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.widget.ArrayAdapter;

public class BizCategoriesLoader extends AsyncTaskLoader<List<BizCategory>> {

	private List<BizCategory> bcList = null; 
	
	public BizCategoriesLoader(Context context) {
		super(context);
		
	}

	@Override
	public List<BizCategory> loadInBackground() {
		if(bcList != null)
			return bcList;
		bcList = new ArrayList<BizCategory>();
		JSONArray jsonCategories = null;
		JSONArray subCategories = null;
		Map<String, BizCategory> categories = null;
		try {
			HttpPostHelper hpe = new HttpPostHelper(4);
			subCategories = hpe.post();
			hpe = new HttpPostHelper(34);
			jsonCategories = hpe.post();
			categories = new HashMap<String, BizCategory>();
			for(int i = 0; i < jsonCategories.length(); i ++){
				JSONObject jobj = jsonCategories.getJSONObject(i);
				BizCategory cat = new BizCategory();
				cat.setId(jobj.getJSONObject("nodes").getString("id"));
				cat.setName(jobj.getJSONObject("nodes").getString("name"));
				categories.put(cat.getId(), cat);
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
		}catch(Exception e){
			e.printStackTrace();
		}
		if(categories != null)
			for(BizCategory bc : categories.values())
				bcList.add(bc);
		
		return bcList;
	}

	@Override
	public void deliverResult(List<BizCategory> list){
		super.deliverResult(list);
	}
	
	@Override 
	protected void onStartLoading() {
		forceLoad();
	}
	
}
