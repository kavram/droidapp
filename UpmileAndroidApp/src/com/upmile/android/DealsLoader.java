package com.upmile.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.upmile.util.HttpPostHelper;

import android.content.Context;
import android.content.AsyncTaskLoader;;

public class DealsLoader extends AsyncTaskLoader<List<DealBean>> {

	
	public DealsLoader(Context context) {
		super(context);
	}

	@Override
	public List<DealBean> loadInBackground() {
		Map<String, DealBean> map = new HashMap<String, DealBean>();
		List<DealBean> list = new ArrayList<DealBean>();
		JSONArray ret = null;
		try {
			HttpPostHelper hpe = new HttpPostHelper(63);
			hpe.addSpParameter("lat", 37.76515);
			hpe.addSpParameter("lon", -122.481);
			hpe.addSpParameter("dist", 45);
			ret = hpe.post();
			for(int i = 0; i < ret.length(); i++){
				JSONObject obj = ret.getJSONObject(i);
				String bizId = obj.getJSONObject("nodes").getString("biz_id");
				if(!map.containsKey(bizId)){
					DealBean db = new DealBean(); 
					db.setBiz(obj.getJSONObject("nodes").getString("biz_name"));
					db.setPhone(obj.getJSONObject("nodes").getString("phone"));
					map.put(bizId, db);
				}
				Deal deal = new Deal();
				String expr = obj.getJSONObject("nodes").getString("expiration");
				SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy h:mm a");
				Date expDate = df.parse(expr);
				deal.setExpiration(expDate);
				deal.setName(obj.getJSONObject("nodes").getString("name"));
				map.get(bizId).getDeals().add(deal);
				
			}
		} catch (Exception e) {

		}
		for(DealBean db : map.values())
			list.add(db);
		
		return list;
	}

	@Override
	public void deliverResult(List<DealBean> list){
		super.deliverResult(list);
	}
	
	@Override 
	protected void onStartLoading() {
		forceLoad();
	}
	
}
