package com.upmile.util;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONObject;

import android.content.Context;

public class FileHelper {
	public final static String USER = "user";
	public final static String BIZ = "biz";
	
	public static void saveData(String name, String data, Context ctx) {
		FileOutputStream fos = null;
		try{
			fos = ctx.openFileOutput(name, Context.MODE_PRIVATE);
			fos.write(data.getBytes());
		}catch(Exception e){
			
		}finally{
			if(fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
			
	}

	public static JSONObject getData(String name, Context ctx){
		BufferedInputStream fis = null;
		byte[] buf = new byte[2500];
		JSONObject ret = null;
		try {
			fis = new BufferedInputStream(ctx.openFileInput(name));
			fis.read(buf);
			ret = new JSONObject(new String(buf));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static void removeData(String name, Context ctx){
		ctx.deleteFile(name);
	}
	
}
