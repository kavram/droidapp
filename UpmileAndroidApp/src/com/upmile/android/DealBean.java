package com.upmile.android;

import java.util.ArrayList;
import java.util.List;

public class DealBean {

	private String biz;
	private String phone;
	private List<Deal> deals;
	
	
	public DealBean(){
	 deals = new ArrayList<Deal>();	
	}
	
	public String getBiz() {
		return biz;
	}
	public void setBiz(String biz) {
		this.biz = biz;
	}
	public List<Deal> getDeals() {
		return deals;
	}
	public void setDeals(List<Deal> deals) {
		this.deals = deals;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
}
