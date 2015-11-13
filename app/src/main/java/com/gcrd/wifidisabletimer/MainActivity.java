package com.gcrd.wifidisabletimer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;



public class MainActivity extends Activity implements View.OnClickListener {

    private ToggleButton disableToggle;
    private WifiManager wifi;
    private PendingIntent pi;
    private BroadcastReceiver br;
    private AlarmManager am;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        disableToggle = (ToggleButton)findViewById(R.id.toggleButton);
        setup();
        disableToggle.setOnClickListener(this);
    }

    private void setup() {
        wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                turnWifiOn();
            }
        };
        registerReceiver(br, new IntentFilter("com.GCRD.wifidisabletimer"));
        pi = PendingIntent.getBroadcast( this, 0, new Intent("com.GCRD.wifidisabletimer"),
                0 );
        am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));

        disableToggle.setOnClickListener(MainActivity.this);
    }


    @Override
    protected void onResume() {
       //set the state of the UI to reflect the real world
        if (wifi.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            disableToggle.setChecked(true);
        } else {
            disableToggle.setChecked(false);
        }
        super.onResume();
    }

    @Override
     protected void onDestroy() {
        am.cancel(pi);
        unregisterReceiver(br);
        super.onDestroy();
    }


    private void turnWifiOn() {
        //reset the state of the button
        disableToggle.setChecked(false);

        //turn on wifi

        wifi.setWifiEnabled(true);

        //clean up alarm tasks
        am.cancel(pi);
    }

    private void turnWifiOff() {
        //set the state of the button
        disableToggle.setChecked(true);

        //disable the wifi
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(false);

    }


    @Override
    public void onClick(View view) {

        // was the toggle off before the press?
        boolean off = ((ToggleButton) view).isChecked();

        if (off) {
            turnWifiOff();
            //calculate period to suspend wifi
            long timeInterval = 0l;
            timeInterval += Long.parseLong(((EditText)findViewById(R.id.hoursField)).getText().toString())*(3600000l);
            timeInterval += Long.parseLong(((EditText)findViewById(R.id.minutesField)).getText().toString())*(60000l);
            timeInterval += Long.parseLong(((EditText)findViewById(R.id.secondsField)).getText().toString())*(1000l);

            // set timer event
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                        timeInterval, pi);
            } else {
                am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                        timeInterval, pi);
            }
        } else {
            turnWifiOn();
        }

    }




}
