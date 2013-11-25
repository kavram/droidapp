package com.upmile.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.AsyncTaskLoader;;

public class DealsLoader extends AsyncTaskLoader<List<DealBean>> {

	public DealsLoader(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<DealBean> loadInBackground() {
		List<DealBean> deals = new ArrayList<DealBean>();
		DealBean db1 = new DealBean();
		db1.setBiz("biz1");
		List<String> biz1deals = new ArrayList<String>();
		biz1deals.add("b1deal1");
		biz1deals.add("b1deal2");
		biz1deals.add("b1deal3");
		db1.setDeals(biz1deals);
		deals.add(db1);
		DealBean db2 = new DealBean();
		db2.setBiz("biz2");
		List<String> biz2deals = new ArrayList<String>();
		biz2deals.add("b2deal1");
		biz2deals.add("b2deal2");
		biz2deals.add("b2deal3");
		biz2deals.add("b2deal4");
		biz2deals.add("b2deal5");
		biz2deals.add("b2deal6");
		biz2deals.add("b2deal7");
		biz2deals.add("b2deal8");
		biz2deals.add("b2deal9");
		biz2deals.add("b2deal10");

		
		db2.setDeals(biz2deals);
		deals.add(db2);
		return deals;
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
