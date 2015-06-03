package com.example.cnwesleywang.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class FtpPushService extends IntentService {

    public FtpPushService() {
        super("FtpPushService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        new PhotoFtpClient(FtpPushService.this).go();
        stopSelf();
    }

}
