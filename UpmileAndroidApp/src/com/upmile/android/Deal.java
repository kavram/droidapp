package com.upmile.android;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Deal {
	private String name;
	private Date expiration; 

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getExpiration() {
		return expiration;
	}
	
	public String getExprLabel(){
		String ret = "";
		SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
		Calendar expCal = Calendar.getInstance();
		expCal.setTime(expiration);
		Calendar cal = Calendar.getInstance();
		if(expCal.get(Calendar.DATE) == cal.get(Calendar.DATE) &&
		 expCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
		 expCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR))
			ret = sdf.format(expiration);
		else{
			cal.add(Calendar.DATE, 1);
			if(expCal.get(Calendar.DATE) == cal.get(Calendar.DATE) &&
			expCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
			expCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR))
				ret = "Tomorrow, " + sdf.format(expiration);
			else{
				sdf = new SimpleDateFormat("MM/dd/yy h:mm a");
				ret = sdf.format(expiration);
			}
		}
		return ret;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	
}
