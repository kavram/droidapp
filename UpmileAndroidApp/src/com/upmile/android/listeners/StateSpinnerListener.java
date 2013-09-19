package com.upmile.android.listeners;

import com.upmile.util.State;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;


public class StateSpinnerListener extends Activity implements OnItemSelectedListener {
	
	private OnStateSelectedHandler handler;
	
	public StateSpinnerListener(OnStateSelectedHandler handler){
		this.handler = handler;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		State st = (State) parent.getItemAtPosition(pos);
		handler.handleStateSelectedEvent(st);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}
	
	public interface OnStateSelectedHandler {
		
		abstract void handleStateSelectedEvent(State state);
	}

}
