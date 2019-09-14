package com.example.asinit_user.gdziejestczoper.services;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.asinit_user.gdziejestczoper.utils.Converters;

import timber.log.Timber;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("GEO_ALARM")){
            Intent geoJobIntent = new Intent(context, GeoJobIntentService.class);
            Timber.d("received broadcast into alarm receiver");
            Converters.appendLog("AlarmReceiver:onReceive for GEO_ALARM");
            GeoJobIntentService.enqueueWork(context, geoJobIntent);

        } else if (intent.getAction().equals("LOG_ALARM")) {
            Intent logJobIntent = new Intent(context, LogJobIntentService.class);
            Timber.d("received broadcast into log receiver");
            Converters.appendLog("AlarmReceiver:onReceive for LOG_ALARM");
            LogJobIntentService.enqueueWork(context, logJobIntent);
        }

    }
}


