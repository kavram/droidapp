package com.upmile.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;

public class HttpPostHelper {

	private JSONObject oper;
	private JSONObject params;
	private JSONArray fields;
	private DefaultHttpClient hc;
	
	public HttpPostHelper(int operId) throws Exception{
		hc =  new DefaultHttpClient();
		oper = new JSONObject();
		oper.accumulate("id", String.valueOf(operId));
		params = new JSONObject();
		fields = new JSONArray();
		oper.accumulate("parameters", params);
		oper.put("fields", fields);		
	}

	public void addFields(JSONObject flds) throws Exception {
		fields.put(flds);
	}

	public void addParameter(String name, Object value) throws Exception{
		JSONObject val = new JSONObject();
		val.accumulate("value", value); 
		params.accumulate(name, val);
	}

	public void addSpParameter(String name, Object value) throws Exception{
		params.accumulate(name, value);
	}
	
	
	public JSONArray post() throws Exception{
		JSONArray ar = new JSONArray();
		ar.put(oper);
		String payload = "payload=" + ar.toString() + "&datatype=json";
		StringEntity se = new StringEntity(payload, "UTF-8");
		se.setContentType("application/json; charset=utf-8");
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json; charset=utf-8"));

		URI uri = new URI("http://10.0.2.2:8080/content");
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setEntity(se);
		HttpResponse hr = hc.execute(httpPost); // httpPost
		JSONArray ret = new JSONArray(readStringEntity(hr));
		return ret;
	}
	
	private String readStringEntity(HttpResponse hr) throws IllegalStateException, IOException{
		HttpEntity he = hr.getEntity();
		InputStream is = he.getContent();
		Reader reader = null;
	    reader = new InputStreamReader(is, "UTF-8");        
	    char[] buffer = new char[(int) he.getContentLength()];
	    reader.read(buffer);
	    return new String(buffer);
	}
}
