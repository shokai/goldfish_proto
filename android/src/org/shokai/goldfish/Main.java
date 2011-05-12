package org.shokai.goldfish;

import java.lang.reflect.Field;

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
            }
            catch(Exception e){
                e.printStackTrace();
                textViewTag.setText("TAG error");
            }
        }
    }
    
    void trace(Object message){
        Log.v("GoldFish", message.toString());
    }
    
}