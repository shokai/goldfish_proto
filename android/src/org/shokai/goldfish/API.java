package org.shokai.goldfish;

import java.util.*;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class API {
    
    public class Action{
        public final static String COPY = "copy";
        public final static String PASTE = "paste";
    }
    
    private String api_url;
    public String getApiUrl(){
        return this.api_url;
    }
    
    public API(String api_url){
        this.api_url = api_url;
    }
    
    public HttpResponse post(String tag, String action) throws Exception{
        HttpClient client = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(this.api_url);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", tag));
        params.add(new BasicNameValuePair("action", action));
        try{
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            return client.execute(httppost);
        }
        catch(Exception e){
            throw e;
        }
    }
}
