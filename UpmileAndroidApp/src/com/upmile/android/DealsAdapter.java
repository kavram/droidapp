package com.upmile.android;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TableRow;
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
        TextView bizView = (TextView) view.findViewById(R.id.biz_view);
        bizView.setText(item.getBiz());
        TableRow tRow = (TableRow) view.findViewById(R.id.trow);
        for(String deal : item.getDeals()){
        	TextView dealView = new TextView(this.getContext());
        	dealView.setPadding(3, 0, 3, 1);
        	dealView.setText(deal);
        	tRow.addView(dealView);
        }
        //GridView gView = (GridView) view.findViewById(R.id.gridview);
        //gView.setNumColumns(item.getDeals().size());
        //ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this.getContext(), R.layout.deals_list_item_layout, R.id.biz_deal_view, item.getDeals());
        
        //gView.setAdapter(adapter2);
        //dListView.setVisibility(ListView.VISIBLE);
        return view;
	} 
	
         
 
	

}
