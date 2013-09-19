package com.upmile.util;

import java.util.ArrayList;
import java.util.List;

public class BizCategory {

	private String id;
	private String name;
	private List<BizSubCategory> subCategories;

	public BizCategory(){
		subCategories = new ArrayList<BizSubCategory>();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<BizSubCategory> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<BizSubCategory> subCategories) {
		this.subCategories = subCategories;
	}
	
	@Override
	public String toString(){
		return name;
	}
}
