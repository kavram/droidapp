package com.upmile.android;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DealsAdapter extends ArrayAdapter<DealBean> {

	private final LayoutInflater mInflater;
	
	public DealsAdapter(Context context) {
		super(context, R.layout.deals_list_item);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View view;
			 
        if (convertView == null) {
            view = mInflater.inflate(R.layout.deals_list_item, parent, false);
        } else {
            view = convertView;
        }

        DealBean item = getItem(position);
        TextView bizNameView = (TextView) view.findViewById(R.id.biz_name);
        bizNameView.setText(item.getBiz());
        TextView bizTelView = (TextView) view.findViewById(R.id.biz_telephone);
        bizTelView.setText(item.getPhone());
        TextView bizAddressView = (TextView) view.findViewById(R.id.biz_address);
        bizAddressView.setText(item.getStreet() + " " + item.getCity() + " " + item.getState() + " " + item.getZipcode());
        LinearLayout hsView = (LinearLayout) view.findViewById(R.id.dealsrow);
        for(Deal deal : item.getDeals()){
        	View dealView = mInflater.inflate(R.layout.deal_view, parent, false);
        	TextView dealTView = (TextView) dealView.findViewById(R.id.deal_name);
        	dealTView.setText(deal.getName());
        	TextView dealExp = (TextView) dealView.findViewById(R.id.deal_expiration);
        	dealExp.setText("Exp: " + deal.getExprLabel());
            TextView dv = new TextView(this.getContext());
            dv.setPadding(3, 0, 3, 1);
            dv.setText(deal.getName());
            hsView.addView(dealView);
        }
        
        return view;
	} 
	
    public void clearDeals(){
    	clear();
    }
 
	

}
