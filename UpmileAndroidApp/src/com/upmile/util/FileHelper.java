package com.upmile.util;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONObject;

import android.content.Context;

public class FileHelper {
	public final static String FILENAME = "user";
	
	public static void saveUser(String user, Context ctx) {
		FileOutputStream fos = null;
		try{
			fos = ctx.openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(user.getBytes());
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

	public static JSONObject getUser(Context ctx){
		BufferedInputStream fis = null;
		byte[] buf = new byte[250];
		JSONObject ret = null;
		try {
			fis = new BufferedInputStream(ctx.openFileInput(FILENAME));
			fis.read(buf);
			ret = new JSONObject(new String(buf));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static void removeUser(Context ctx){
		ctx.deleteFile(FILENAME);
	}
	
}
