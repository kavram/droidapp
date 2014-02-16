package com.upmile.android;

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

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	
}
