package org.shokai.goldfish;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.http.*;

import com.google.common.primitives.UnsignedBytes;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.*;
import android.widget.TextView;
import android.hardware.*;

public class Main extends Activity implements SensorEventListener{
    private TextView textViewTag;
    private final String api_url = "http://10.0.1.5:8930";
    private SensorManager sm;
    private API api;

    @Override
    public void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        trace("start");
        textViewTag = (TextView)findViewById(R.id.textViewTag);
        textViewTag.setText("no TAG");
        sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        api = new API(api_url);
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
                api.post(id, API.Action.COPY);
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
    
    @Override
    protected void onResume() {
        super.onResume();
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            Sensor sensor = sensors.get(0);
            sm.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    protected void onStop() {
        sm.unregisterListener(this);
        super.onStop();
    }
    

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            trace("onSensorChanged : " + event.sensor.getName() + " : " + 
                    ",x:" + event.values[0] + 
                    ", y:" + event.values[1] + 
                    ", z:" + event.values[2]);
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        trace("onAccuracyChanged : " + accuracy);
    }
    
}