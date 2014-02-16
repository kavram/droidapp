package com.upmile.util;

import java.io.File;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.mime.HttpMultipartMode;

public class HttpFormSubmitHelper {
	
	HttpPost httpPost;
	BasicHttpParams httpParams;
	MultipartEntityBuilder mpEntity;
	
	public HttpFormSubmitHelper(String path) throws Exception{
		URI uri = new URI(HttpPostHelper.POST_DOMAIN + path);
		httpPost = new HttpPost(uri);
		mpEntity = MultipartEntityBuilder.create();
	}

	public void addStringParameter(String name, String value) throws Exception{
		mpEntity.addTextBody(name, value);
	}
	
	public void addFileParameter(String name, File file){
		mpEntity.addBinaryBody(name, file);
	}
	
	public HttpResponse post() throws Exception {
		DefaultHttpClient hc =  new DefaultHttpClient();
		httpPost.setEntity(mpEntity.build());
		return hc.execute(httpPost);
	}
	
}
