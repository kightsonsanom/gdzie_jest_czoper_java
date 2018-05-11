package com.example.asinit_user.gdziejestczoper.services;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.example.asinit_user.gdziejestczoper.db.Repository;
import com.example.asinit_user.gdziejestczoper.db.SharedPreferencesRepo;
import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;
import com.example.asinit_user.gdziejestczoper.viewobjects.PositionGeoJoin;
import com.example.asinit_user.gdziejestczoper.utils.Constants;
import com.example.asinit_user.gdziejestczoper.utils.Converters;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import timber.log.Timber;

public class GeoJobIntentService extends JobIntentService implements PositionManagerCallback {

    static final int JOB_ID = 1000;

    private Context context;

    private final static float ACCEPTABLE_DISTANCE_BETWEEN_GEO = 150f;
    // kiedy konczy sie ruch i zaczyna postoj to postoj musi byc bardziej aktualny od ruchu
    private static final long NEW_POSITION_OFFSET = 1;

    private AddressResultReceiver addressResultReceiver = new AddressResultReceiver(new Handler());
    private String locationAddress;

    @Inject
    Repository repository;

    @Inject
    SharedPreferencesRepo sharedPreferencesRepo;

    Geo newGeo;
    Geo latestGeoFromDb;
    Position newPosition;
    Position latestPositionFromDb;
    private int userID;
    private Thread lookForLocation;
    private final Object lock = new Object();
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
        repository.setPositionManagerCallback(this);
        context = getApplicationContext();
        Timber.d("starting localization service");
        userID = sharedPreferencesRepo.getUserID();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Timber.d("new location arrived");
                assignLocationToGeo(locationResult.getLastLocation());
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        LocationRequest locationRequest = LocationRequest.create();
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        lookForLocation = new Thread(() -> {
            Timber.d("lock = " + lock);
            synchronized (lock) {

                try {
                    Timber.d("start waiting in onCreate");
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        lookForLocation.start();
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GeoJobIntentService.class, JOB_ID, work);
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        try {
            Timber.d("waiting for thread to finish in onHandleWork");
            lookForLocation.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Timber.d("thread finished in onHandleWork");
    }

    @Override
    public void onDestroy() {
        Timber.d("getjobintentservice on destroy");
        super.onDestroy();
    }

    public void setLocationPosition() {
        synchronized (lock) {
            sendGeo(newGeo);

            if (latestGeoFromDb == null || latestPositionFromDb == null) {
                Timber.d("latestPositionFromDb is null lub geo za stare");

                newPosition = new Position(userID);
                newPosition.setStartLocation(locationAddress);
                newPosition.setStartDate(Converters.longToString(newGeo.getDate()));
                newPosition.setFirstLocationDate(newGeo.getDate());
                newPosition.setStatus("Nieznany");
                newPosition.setLastLocationDate(newGeo.getDate());

                sendPosition(newPosition);

            } else if (isLatestGeoFromDbTooOld()) {
                Timber.d("Geo za stare");
                newPosition = new Position(userID);
                newPosition.setStartDate(Converters.longToString(latestGeoFromDb.getDate()));
                newPosition.setEndDate(Converters.longToString(newGeo.getDate()));
                newPosition.setFirstLocationDate(newGeo.getDate());
                newPosition.setStatus("Przerwa");

                sendPosition(newPosition);

                newPosition = new Position(userID);
                newPosition.setStartLocation(locationAddress);
                newPosition.setStartDate(Converters.longToString(newGeo.getDate()));
                newPosition.setFirstLocationDate(newGeo.getDate());
                newPosition.setStatus("Nieznany");
                newPosition.setLastLocationDate(newGeo.getDate());

                sendPosition(newPosition);

            } else if (latestPositionFromDb.getStatus().equals("Nieznany")) {

                Timber.d("status geo nieznany");

                if (isLastGeoFarAway()) {
                    Timber.d("bylo przemieszczenie");
                    latestPositionFromDb.setStatus("Ruch");
                    latestPositionFromDb.setEndLocation(locationAddress);

                } else {
                    Timber.d("nie bylo przemieszczenia");
                    latestPositionFromDb.setEndDate(Converters.longToString(newGeo.getDate()));
                    latestPositionFromDb.setStatus("Postój");
                }

                latestPositionFromDb.setLastLocationDate(newGeo.getDate());
                updatePosition(latestPositionFromDb);


            } else if (latestPositionFromDb.getStatus().equals("Postój")) {
                Timber.d("status geo postój");

                latestPositionFromDb.setEndDate(Converters.longToString(newGeo.getDate()));
                latestPositionFromDb.setLastLocationDate(newGeo.getDate());
                updatePosition(latestPositionFromDb);

                if (isLastGeoFarAway()) {
                    Timber.d("bylo przemieszczenie");

                    newPosition = new Position(userID);
                    newPosition.setStatus("Ruch");
                    newPosition.setStartLocation(locationAddress);
                    newPosition.setLastLocationDate(newGeo.getDate() + NEW_POSITION_OFFSET);
                    newPosition.setFirstLocationDate(newGeo.getDate());
                    newPosition.setStartDate(Converters.longToString(newGeo.getDate()));
                    sendPosition(newPosition);
                }

            } else if (latestPositionFromDb.getStatus().equals("Ruch")) {
                Timber.d("status geo ruch");

                latestPositionFromDb.setLastLocationDate(newGeo.getDate());
                latestPositionFromDb.setEndDate(Converters.longToString(newGeo.getDate()));
                latestPositionFromDb.setEndLocation(locationAddress);
                updatePosition(latestPositionFromDb);

                if (isLastGeoFarAway()) {
                    Timber.d("bylo przemieszczenie");

                } else {
                    Timber.d("nie bylo przemieszczenia");


                    newPosition = new Position(userID);
                    newPosition.setStatus("Postój");
                    newPosition.setStartLocation(locationAddress);
                    newPosition.setFirstLocationDate(newGeo.getDate());
                    newPosition.setStartDate(Converters.longToString(newGeo.getDate()));
                    newPosition.setLastLocationDate(newGeo.getDate() + NEW_POSITION_OFFSET);
                    sendPosition(newPosition);
                }

            } else {
                Timber.d("Zaden status sie nie zgadzal");
                throw new RuntimeException("Wrong position status");
            }

            if (newPosition != null) {
                PositionGeoJoin positionGeoJoin = new PositionGeoJoin(newPosition.getPosition_id(), newGeo.getGeo_id(), newGeo.getDate());
                Timber.d("assignGeoToPosition = " + positionGeoJoin.toString());
                assignGeoToPosition(positionGeoJoin);
            } else {
                PositionGeoJoin positionGeoJoin = new PositionGeoJoin(latestPositionFromDb.getPosition_id(), newGeo.getGeo_id(), newGeo.getDate());
                Timber.d("assignGeoToPosition = " + positionGeoJoin.toString());
                assignGeoToPosition(positionGeoJoin);
            }


            mFusedLocationClient.removeLocationUpdates(locationCallback);

            lock.notify();
        }
    }

    private void updatePosition(Position position) {
        repository.updatePosition(position);
    }

    private void assignGeoToPosition(PositionGeoJoin positionGeoJoin) {
        repository.assignGeoToPosition(positionGeoJoin);
    }

    private boolean isLastGeoFarAway() {
        float distance = newGeo.getLocation().distanceTo(latestGeoFromDb.getLocation());
        Timber.d("distance between locations = " + distance);
        return distance > ACCEPTABLE_DISTANCE_BETWEEN_GEO;
    }

    public void assignLocationToGeo(Location location) {
        newGeo = new Geo(location, userID);
        startGeoCodingService(location);
    }

    public void startGeoCodingService(Location location){
        Timber.d("startGeoCodingService");
        Intent intent = new Intent(context, GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        context.startService(intent);
    }

    private void sendPosition(Position newPosition) {
        repository.postPosition(newPosition);
    }


    private boolean isLatestGeoFromDbTooOld() {
        return (System.currentTimeMillis() - latestPositionFromDb.getLastLocationDate()) > 10800000;
    }

    private void sendGeo(Geo newGeo) {
        repository.postGeo(newGeo);
    }


    private void getOldestGeoForPositionFromDb() {
        repository.getOldestGeoForPosition(latestPositionFromDb.getPosition_id());
    }


    private void getLastGeoFromDb() {
        Timber.d("getLastGeoFromDb");
        repository.getLatestGeo();
    }

    @Override
    public void setLatestGeoFromDb(Geo geo) {
        latestGeoFromDb = geo;
        setLocationPosition();
    }

    @Override
    public void setNewLocation(Location location) {
        assignLocationToGeo(location);
    }

    private void getLastPositionFromDb() {
        Timber.d("getLastPositionFromDb");
        repository.getLatestPosition();
    }

    @Override
    public void setLatestPositionFromDb(Position position) {
        Timber.d("setLatestPositionFromDb");
        latestPositionFromDb = position;
        if (position != null) {
            String positionStatus = position.getStatus();
            if (positionStatus.equals("Ruch") || positionStatus.equals("Nieznany")) {
                getLastGeoFromDb();
            } else if (positionStatus.equals("Postój")) {
                getOldestGeoForPositionFromDb();
            }
        } else {
            getLastGeoFromDb();
        }

    }


    public class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case Constants.SUCCESS_RESULT:
                    locationAddress = resultData.getString(Constants.RESULT_DATA_KEY);
                    getLastPositionFromDb();
                    break;
                case Constants.FAILURE_RESULT:
                    Timber.d("Repeat geocoding service");
                    Location location = resultData.getParcelable("location");
                    startGeoCodingService(location);
                    break;
                default:
            }

        }
    }


}