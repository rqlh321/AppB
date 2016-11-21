package com.example.sic.appb;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sic on 21.11.2016.
 */

public class MainService extends Service {
    public final static String TAG = "ProgForce";
    public static final String MASSAGE_FROM_APP_B = "info_from_app_B";
    public static final String APP_RECIPIENT = "com.example.sic.appa";
    public static final String PID = "0";
    public static final String WIFI_STATE = "1";
    public static final String SCREEN_STATE = "2";

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Log.d(TAG, "SecondApp:: Received info at " + new Date() + " from process " + bundle.getInt(PID) + ", WiFiState = " + bundle.getInt(WIFI_STATE));
        }
    };
    final private Messenger myMessenger = new Messenger(mHandler);
    private Timer timer = new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final int pid = android.os.Process.myPid();
        final PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        final Intent appBMassageIntent = new Intent(MASSAGE_FROM_APP_B);
        appBMassageIntent.setPackage(APP_RECIPIENT);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int screenStatus = powerManager.isScreenOn() ? 1 : 0;
                appBMassageIntent.putExtra(PID, pid);
                appBMassageIntent.putExtra(SCREEN_STATE, screenStatus);
                sendBroadcast(appBMassageIntent);
                Log.d(TAG, "SecondApp:: Sent info at" + new Date() + ", ScreenState = " + screenStatus);
            }
        }, 0, 1000 * 60 * 2);

        Toast.makeText(this, "Hello from App B Service!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        Toast.makeText(this, "By from App B Service!", Toast.LENGTH_SHORT).show();
    }

}
