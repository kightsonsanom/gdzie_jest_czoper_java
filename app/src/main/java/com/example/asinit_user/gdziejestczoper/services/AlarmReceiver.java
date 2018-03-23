package com.example.asinit_user.gdziejestczoper.services;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import timber.log.Timber;


public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        synchronized (this) {
            OnSuccessListener onSuccessListener = (OnSuccessListener<Location>) location -> {
                Timber.d("new location arrived to listener = " + location.toString());

                intent.putExtra("location", location);
                GeoJobIntentService.enqueueWork(context, intent);

            };

            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(onSuccessListener);
        }
    }

}
