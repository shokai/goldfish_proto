package org.shokai.goldfish;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;

import com.google.common.primitives.UnsignedBytes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.*;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.*;

public class Main extends Activity implements SensorEventListener {
    private TextView textViewTag;
    private ImageView imageViewTop;
    // private final String api_url = "http://192.168.1.38:8930";
    private final String api_url = "http://dev.shokai.org:8930";
    private SensorManager sm;
    private API api;
    private String tag_id = null;
    private boolean api_alread_posted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        trace("start");
        textViewTag = (TextView) findViewById(R.id.textViewTag);
        textViewTag.setText("please touch NFC tag");
        imageViewTop = (ImageView) findViewById(R.id.imageViewTop);
        sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        api = new API(api_url);
        resolveIntent(this.getIntent());
    }

    void resolveIntent(Intent intent) {
        String action = intent.getAction();
        trace(action);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            try {
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
                this.tag_id = id;
                textViewTag.setText("TAG : " + tag_id);
                trace(tag_id);
            }
            catch (Exception e) {
                e.printStackTrace();
                textViewTag.setText("TAG error");
            }
        }
    }

    void trace(Object message) {
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
        if(this.api_alread_posted || this.tag_id == null) return;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            try {
                String res = "";
                if (x > 4) { // paste
                    trace("paste");
                    res = api.post(tag_id, API.Action.PASTE);
                    this.api_alread_posted = true;
                    trace(res);
                    finish();
                }
                else if (x < -4) { // copy
                    trace("copy");
                    res = api.post(tag_id, API.Action.COPY);
                    this.api_alread_posted = true;
                    trace(res);
                    this.textViewTag.setText(res);
                    if(Pattern.compile("^https?://.+").matcher(res).matches()){
                        Uri uri = Uri.parse(res);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        finish();
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        trace("onAccuracyChanged : " + accuracy);
    }

}