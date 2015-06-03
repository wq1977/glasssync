package com.example.cnwesleywang.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class PowerConnectionReceiver extends BroadcastReceiver {
    public PowerConnectionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("wesley","onReceive!!!!!");
        context.startService(new Intent(context,FtpPushService.class));
    }
}
