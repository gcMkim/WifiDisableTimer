package com.sample.mkim.wifidisabletimer;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends ActionBarActivity {

    private ToggleButton disableToggle;
    private long timeInterval = 0l;
    private Timer timer;
    final Handler myHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        disableToggle = (ToggleButton)findViewById(R.id.toggleButton);
        timer = new Timer(true);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    private void updateGUI() {
        myHandler.post(myRunnable);
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            disableToggle.setChecked(false);
        }
    };



    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean off = !((ToggleButton) view).isChecked();

        if (off) {
            // reenable wifi
            WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(true);
            timer.cancel();
            timer = null;
            disableToggle.setChecked(false);
        } else {
            // Disable wifi for period of time
            timeInterval += Long.parseLong(((EditText)findViewById(R.id.hoursField)).getText().toString())*(3600000l);
            timeInterval += Long.parseLong(((EditText)findViewById(R.id.minutesField)).getText().toString())*(60000l);
            timeInterval += Long.parseLong(((EditText)findViewById(R.id.secondsField)).getText().toString())*(1000l);

            WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(false);
            disableToggle.setChecked(true);
            timer = new Timer(true);
            timer.schedule(new DelayTimer(), timeInterval);
        }
    }


    private class DelayTimer extends TimerTask {

        @Override
        public void run() {
            WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(true);
            timer.cancel();
            timer = null;
            updateGUI();
        }
    }

}
