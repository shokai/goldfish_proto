package org.shokai.goldfish;

import jp.co.topgate.android.nfc.TagWrapper;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.*;

public class Main extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        trace("start");
        resolveIntent(this.getIntent());
    }

    void resolveIntent(Intent intent) {
        String action = intent.getAction();
        trace(action);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            try{
                Parcelable tag = intent.getParcelableExtra("android.nfc.extra.TAG");
                TagWrapper tw = new TagWrapper(tag);
                String id = tw.getHexIDString();
                trace(id);
            }
            catch(Exception e){
                trace(e);
            }
        }
    }
    
    void trace(Object message){
        Log.v("GoldFish", message.toString());
    }
}