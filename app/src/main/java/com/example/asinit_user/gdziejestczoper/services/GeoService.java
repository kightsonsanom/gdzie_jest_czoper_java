package com.example.asinit_user.gdziejestczoper.services;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.example.asinit_user.gdziejestczoper.R;
import com.example.asinit_user.gdziejestczoper.db.entities.Geo;
import com.example.asinit_user.gdziejestczoper.ui.mainView.NavigationActivity;
import com.example.asinit_user.gdziejestczoper.utils.Constants;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import timber.log.Timber;

public class GeoService extends Service {

    public static final int GEO_SERVICE_FREQUENCY = 300000;
    public static boolean GPSserviceStarted;
    private Context context;

    @Inject
    PositionManager positionManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
        context = getApplicationContext();


        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Timber.d("new location arrived to listener = " + location.toString());
                positionManager.getLocationAddress(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }


            //if GPS is not working on the device take user to the settings menu
            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Timber.d("starting localization service");
        /*
        noinspection MissingPermission
        sprawdzamy tylko co jakiś czas, a nie co czas i przebytą drogę, żeby sprawdzać czy był postój czy ruch na podstawie
        drogi od jakiej się oddaliliśmy od ostatniego punktu, a nie czasu kiedy była ostatnia geolokalizacja

        */

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GEO_SERVICE_FREQUENCY, 0, locationListener);
        GPSserviceStarted = true;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }


    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, NavigationActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);


        Notification notification =
                new Notification.Builder(this)
                        .setContentTitle("Lokalizacja GdzieJestCzoper")
                        .setContentText("Sory musze")
                        .setSmallIcon(R.mipmap.face_round)
                        .setContentIntent(pendingIntent)
                        .setTicker("jakis ticker")
                        .build();


        startForeground(Constants.SERVICE_NOTIFICATION_ID, notification);


        return START_STICKY;
    }
}

