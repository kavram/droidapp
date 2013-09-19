package com.upmile.util;

import java.io.File;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.mime.HttpMultipartMode;

public class HttpFormSubmitHelper {
	
	HttpPost httpPost;
	BasicHttpParams httpParams;
	MultipartEntity mpEntity;
	
	public HttpFormSubmitHelper(String path) throws Exception{
		URI uri = new URI("http://192.168.1.47:8081" + path);
		httpPost = new HttpPost(uri);
		mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
	}

	public void addStringParameter(String name, String value) throws Exception{
		mpEntity.addPart(name, new StringBody(value));
	}
	
	public void addFileParameter(String name, File file){
		mpEntity.addPart(name, new FileBody(file, "image/jpeg"));
	}
	
	public HttpResponse post() throws Exception {
		DefaultHttpClient hc =  new DefaultHttpClient();
		httpPost.setEntity(mpEntity);
		return hc.execute(httpPost);
	}
	
}
