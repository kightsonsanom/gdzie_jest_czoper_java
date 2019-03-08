package com.example.asinit_user.gdziejestczoper.services;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.example.asinit_user.gdziejestczoper.db.Repository;
import com.example.asinit_user.gdziejestczoper.db.SharedPreferencesRepository;
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

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import timber.log.Timber;

public class GeoJobIntentService extends JobIntentService implements PositionManagerCallback {

    static final int JOB_ID = 1000;
    public static final int NO_GEO_BREAK_15MIN = 900000;
    public static final int STATUS_NIEZNANY = 2;
    public static final int STATUS_POSTOJ = 1;
    public static final int STATUS_PRZERWA = 3;
    public static final int STATUS_RUCH = 0;

    private Context context;

    private final static float ACCEPTABLE_DISTANCE_BETWEEN_GEO = 250f;
    // kiedy konczy sie ruch i zaczyna postoj to postoj musi byc bardziej aktualny od ruchu
    private static final long NEW_POSITION_OFFSET = 1;

    private AddressResultReceiver addressResultReceiver = new AddressResultReceiver(new Handler());
    private String locationAddress;

    @Inject
    Repository repository;

    @Inject
    SharedPreferencesRepository sharedPreferencesRepository;

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
        userID = sharedPreferencesRepository.getUserID();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                startProcessingGeo(locationResult.getLastLocation());
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
            try {

                sendGeo(newGeo);

                if (latestGeoFromDb == null || latestPositionFromDb == null) {
                    Timber.d("latestPositionFromDb is null lub geo za stare");

                    newPosition = new Position(userID);
                    newPosition.setStartLocation(locationAddress);
                    newPosition.setStartDate(Converters.longToString(newGeo.getDate()));
                    newPosition.setFirstLocationDate(newGeo.getDate());
                    newPosition.setStatus(STATUS_NIEZNANY);
                    newPosition.setLastLocationDate(newGeo.getDate());

                    sendPosition(newPosition);

                } else if (isLatestGeoFromDbTooOld()) {
                    Timber.d("Geo za stare");
                    newPosition = new Position(userID);
                    newPosition.setStartDate(Converters.longToString(latestGeoFromDb.getDate()));
                    newPosition.setEndDate(Converters.longToString(newGeo.getDate()));
                    newPosition.setFirstLocationDate(newGeo.getDate());
                    newPosition.setLastLocationDate(newGeo.getDate());
                    newPosition.setStatus(STATUS_PRZERWA);

                    sendPosition(newPosition);

                    newPosition = new Position(userID);
                    newPosition.setStartLocation(locationAddress);
                    newPosition.setStartDate(Converters.longToString(newGeo.getDate() + NEW_POSITION_OFFSET));
                    newPosition.setFirstLocationDate(newGeo.getDate() + NEW_POSITION_OFFSET);
                    newPosition.setStatus(STATUS_NIEZNANY);
                    newPosition.setLastLocationDate(newGeo.getDate() + NEW_POSITION_OFFSET);

                    sendPosition(newPosition);

                } else if (latestPositionFromDb.getStatus() == STATUS_NIEZNANY) {

                    Timber.d("status geo nieznany");

                    if (isLastGeoFarAway()) {
                        Timber.d("bylo przemieszczenie");
                        latestPositionFromDb.setStatus(STATUS_RUCH);
                        latestPositionFromDb.setEndLocation(locationAddress);
                        latestPositionFromDb.setEndDate(Converters.longToString(newGeo.getDate()));


                    } else {
                        Timber.d("nie bylo przemieszczenia");
                        latestPositionFromDb.setEndDate(Converters.longToString(newGeo.getDate()));
                        latestPositionFromDb.setStatus(STATUS_POSTOJ);
                    }

                    latestPositionFromDb.setLastLocationDate(newGeo.getDate());
                    updatePosition(latestPositionFromDb);


                } else if (latestPositionFromDb.getStatus() == STATUS_POSTOJ) {
                    Timber.d("status geo postój");

                    if (isLastGeoFarAway()) {
                        Timber.d("bylo przemieszczenie");
                        newPosition = new Position(userID);
                        newPosition.setStatus(STATUS_RUCH);
                        newPosition.setStartLocation(latestPositionFromDb.getStartLocation());
                        newPosition.setEndLocation(locationAddress);
                        newPosition.setFirstLocationDate(newGeo.getDate());
                        newPosition.setLastLocationDate(newGeo.getDate() + NEW_POSITION_OFFSET);
                        newPosition.setStartDate(latestPositionFromDb.getEndDate());
                        newPosition.setEndDate(Converters.longToString(newGeo.getDate()));
                        sendPosition(newPosition);
                    } else {
                        latestPositionFromDb.setEndDate(Converters.longToString(newGeo.getDate()));
                        latestPositionFromDb.setLastLocationDate(newGeo.getDate());
                        if (latestPositionFromDb.getStartLocation().equals(Constants.GEOCODING_FAILURE)) {
                            latestPositionFromDb.setStartLocation(locationAddress);
                        }
                        updatePosition(latestPositionFromDb);
                    }

                } else if (latestPositionFromDb.getStatus() == STATUS_RUCH) {
                    Timber.d("status geo ruch");

                    latestPositionFromDb.setLastLocationDate(newGeo.getDate());
                    latestPositionFromDb.setEndLocation(locationAddress);
                    updatePosition(latestPositionFromDb);

                    if (isLastGeoFarAway()) {
                        Timber.d("bylo przemieszczenie");

                    } else {
                        Timber.d("nie bylo przemieszczenia");

                        newPosition = new Position(userID);
                        newPosition.setStatus(STATUS_POSTOJ);
                        newPosition.setStartLocation(locationAddress);
                        newPosition.setFirstLocationDate(newGeo.getDate());
                        newPosition.setStartDate(latestPositionFromDb.getEndDate());
                        newPosition.setEndDate(Converters.longToString(newGeo.getDate()));
                        newPosition.setLastLocationDate(newGeo.getDate() + NEW_POSITION_OFFSET);
                        sendPosition(newPosition);
                    }

                } else {
                    Timber.d("Zaden status sie nie zgadzal");
                    throw new RuntimeException("Wrong position status");
                }

                if (newPosition != null) {
                    PositionGeoJoin positionGeoJoin = new PositionGeoJoin(newPosition.getId(), newGeo.getId(), newGeo.getDate());
                    Timber.d("assignGeoToPosition = " + positionGeoJoin.toString());
                    assignGeoToPosition(positionGeoJoin);
                } else {
                    PositionGeoJoin positionGeoJoin = new PositionGeoJoin(latestPositionFromDb.getId(), newGeo.getId(), newGeo.getDate());
                    Timber.d("assignGeoToPosition = " + positionGeoJoin.toString());
                    assignGeoToPosition(positionGeoJoin);
                }


                mFusedLocationClient.removeLocationUpdates(locationCallback);
            } catch (NullPointerException e){
                String values = "latestPositionFromDB = " + latestPositionFromDb +
                        "\nlatestGeoFromDB = " + latestGeoFromDb +
//                        "\nuserID = " + repository.getUserID() +
                        "\nrepository = " + repository;

                sharedPreferencesRepository.setErrorValue(values);
            }

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

    public void startProcessingGeo(Location location) {
        newGeo = new Geo(location, userID, location.getTime());
        startGeoCodingService(location);
    }

    public void startGeoCodingService(Location location){
        Intent intent = new Intent(context, GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        context.startService(intent);
    }

    private void sendPosition(Position newPosition) {
        repository.postPosition(newPosition);
    }


    private boolean isLatestGeoFromDbTooOld() {
        return (newGeo.getDate() - latestPositionFromDb.getLastLocationDate()) > NO_GEO_BREAK_15MIN;
    }

    private void sendGeo(Geo newGeo) {
        repository.postGeo(newGeo);
    }


    private void getOldestGeoForPositionFromDb() {
        repository.getOldestGeoForPosition(latestPositionFromDb.getId());
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
        Timber.d("nowe location z łapy");
        startProcessingGeo(location);
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
            int positionStatus = position.getStatus();
            if (positionStatus == STATUS_RUCH || positionStatus == STATUS_NIEZNANY) {
                getLastGeoFromDb();
            } else if (positionStatus == STATUS_POSTOJ) {
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