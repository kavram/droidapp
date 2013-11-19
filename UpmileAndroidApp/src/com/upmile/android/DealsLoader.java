package com.upmile.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.AsyncTaskLoader;;

public class DealsLoader extends AsyncTaskLoader<List<String>> {

	public DealsLoader(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<String> loadInBackground() {
		List<String> list = new ArrayList<String>();
		list.add("first");
		list.add("second");
		list.add("third");
		return list;
	}

	@Override
	public void deliverResult(List<String> list){
		super.deliverResult(list);
	}
	
	@Override 
	protected void onStartLoading() {
		forceLoad();
	}
	
}
