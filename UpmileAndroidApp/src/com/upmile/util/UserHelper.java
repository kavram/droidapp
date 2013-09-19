package com.upmile.util;

import org.json.JSONObject;

import android.content.Context;

public class UserHelper {
	private static JSONObject user = null;
	
	public static void intUser(Context ctx){
		JSONObject user = FileHelper.getUser(ctx);
	}
	
	
}
