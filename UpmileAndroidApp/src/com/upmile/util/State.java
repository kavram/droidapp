package com.upmile.util;

import java.util.ArrayList;
import java.util.List;

public class State {
	private String name;
	private String label;
	
	
	public State(String name, String label){
		this.name = name;
		this.label = label;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	public static List<State> getStates(){
		List<State> list = new ArrayList<State>();
		list.add(new State("Select State", "state"));
		list.add(new State("Alaska", "AK"));
		list.add(new State("Arizona", "AZ"));
		list.add(new State("Arkansas", "AR"));
		list.add(new State("California", "CA"));
		list.add(new State("Colorado", "CO"));
		list.add(new State("Connecticut", "CT"));
		list.add(new State("Delaware", "DE"));
		list.add(new State("District of Columbia", "DC"));
		list.add(new State("Florida", "FL"));
		list.add(new State("Georgia", "GA"));
		list.add(new State("Hawaii", "HI"));
		list.add(new State("Idaho", "ID"));
		list.add(new State("Illinois", "IL"));
		list.add(new State("Indiana", "IN"));
		list.add(new State("Iowa", "IW"));
		list.add(new State("Kansas", "KS"));
		list.add(new State("Kentucky", "KT"));
		list.add(new State("Louisiana", "LA"));
		list.add(new State("Maine", "ME"));
		return list;
	}
	
}
