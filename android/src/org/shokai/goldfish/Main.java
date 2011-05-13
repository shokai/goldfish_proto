package org.shokai.goldfish;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.primitives.UnsignedBytes;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.*;
import android.widget.TextView;

public class Main extends Activity {
    private TextView textViewTag;
    private final String api_url = "http://192.168.1.38:8930";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        trace("start");
        textViewTag = (TextView)findViewById(R.id.textViewTag);
        textViewTag.setText("no TAG");
        resolveIntent(this.getIntent());
    }

    void resolveIntent(Intent intent) {
        String action = intent.getAction();
        trace(action);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            try{
                Parcelable tag = intent.getParcelableExtra("android.nfc.extra.TAG");
                Field f = tag.getClass().getDeclaredField("mId");
                f.setAccessible(true);
                byte[] mId = (byte[]) f.get(tag);
                StringBuilder sb = new StringBuilder();
                for (byte id : mId) {
                    String hexString = Integer.toHexString(UnsignedBytes.toInt(id));
                    if (hexString.length() == 1) sb.append("0");
                    sb.append(hexString);
                }
                String id = sb.toString();
                textViewTag.setText("TAG : "+id);
                trace(id);
                this.post_nfctag(id);
            }
            catch(Exception e){
                e.printStackTrace();
                textViewTag.setText("TAG error");
            }
        }
    }
    
    public HttpResponse post_nfctag(String tag) throws Exception{
        HttpClient client = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(this.api_url);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", tag));
        try{
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            return client.execute(httppost);
        }
        catch(Exception e){
            throw e;
        }
    }
    
    void trace(Object message){
        Log.v("GoldFish", message.toString());
    }
    
}