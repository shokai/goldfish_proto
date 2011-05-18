package org.shokai.goldfish;

import java.util.*;
import java.io.*;

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
    
    public String copy(String tag) throws Exception{
        try{
            return this.post(tag, Action.COPY, null);
        }
        catch(Exception e){
            throw e;
        }
    }
    
    public String paste(String tag, String clipboard) throws Exception{
        try{
            return this.post(tag, Action.PASTE, clipboard);
        }
        catch(Exception e){
            throw e;
        }
    }
    
    public String post(String tag, String action, String clipboard) throws Exception{
        HttpClient client = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(this.api_url+"/android");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", tag));
        params.add(new BasicNameValuePair("action", action));
        if(action.equals(Action.PASTE)){
            params.add(new BasicNameValuePair("clip", clipboard));
        }
        try{
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse res = client.execute(httppost);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            res.getEntity().writeTo(os);
            return os.toString();
        }
        catch(Exception e){
            throw e;
        }
    }
}
