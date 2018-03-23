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
import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;
import com.example.asinit_user.gdziejestczoper.viewobjects.PositionGeoJoin;
import com.example.asinit_user.gdziejestczoper.utils.Constants;
import com.example.asinit_user.gdziejestczoper.utils.Converters;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import timber.log.Timber;

public class GeoJobIntentService extends JobIntentService implements PositionManagerCallback {

    static final int JOB_ID = 1000;

    public static final int GEO_SERVICE_FREQUENCY = 3000;
    public static boolean GPSserviceStarted;
    private Context context;

    private final static float ACCEPTABLE_DISTANCE_BETWEEN_GEO = 125f;
    // kiedy konczy sie ruch i zaczyna postoj to postoj musi byc bardziej aktualny od ruchu
    private static final long NEW_POSITION_OFFSET = 1;

//    @Inject
//    Repository repository;

    private AddressResultReceiver addressResultReceiver = new AddressResultReceiver(new Handler());
    private String locationAddress;

    @Inject
    Repository repository;


    Geo newGeo;
    Geo latestGeoFromDb;
    Position newPosition;
    Position latestPositionFromDb;
    private FusedLocationProviderClient mFusedLocationClient;
    private OnSuccessListener onSuccessListener;
    private boolean completed = false;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
        repository.setPositionManagerCallback(this);
        context = getApplicationContext();
        Timber.d("starting localization service");

//        onSuccessListener = new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                Timber.d("new location arrived to listener = " + location.toString());
//                getLocationAddress(location);
//
//            }
//        };
//
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mFusedLocationClient.getLastLocation().addOnSuccessListener(onSuccessListener);
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GeoJobIntentService.class, JOB_ID, work);
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        synchronized (new Object()) {
            Location location = intent.getParcelableExtra("location");
            getLocationAddress(location);
        }
    }

    @Override
    public void onDestroy() {
        Timber.d("getjobintentservice on destroy");
        super.onDestroy();
    }

    public void setLocationPosition() {
        sendGeo(newGeo);

        if (latestGeoFromDb == null) {
            Timber.d("latestPositionFromDb is null lub geo za stare");

            newPosition = new Position();
            newPosition.setStartLocation(locationAddress);
            newPosition.setStartDate(Converters.longToString(newGeo.getDate()));
            newPosition.setStatus("Nieznany");
            newPosition.setLastLocationDate(newGeo.getDate());

            sendPosition(newPosition);

        } else if (isLatestGeoFromDbTooOld()) {
            Timber.d("Geo za stare");
            newPosition = new Position();
            newPosition.setStartDate(Converters.longToString(latestGeoFromDb.getDate()));
            newPosition.setEndDate(Converters.longToString(newGeo.getDate()));
            newPosition.setStatus("Przerwa");

            sendPosition(newPosition);

            newPosition = new Position();
            newPosition.setStartLocation(locationAddress);
            newPosition.setStartDate(Converters.longToString(newGeo.getDate()));
            newPosition.setStatus("Nieznany");
            newPosition.setLastLocationDate(newGeo.getDate());

            sendPosition(newPosition);

        } else if (latestPositionFromDb.getStatus().

                equals("Nieznany"))

        {
            Timber.d("status geo nieznany");

            if (isLastGeoFarAway()) {
                Timber.d("bylo przemieszczenie");
                latestPositionFromDb.setStatus("Ruch");
                latestPositionFromDb.setLastLocationDate(newGeo.getDate());

            } else {
                Timber.d("nie bylo przemieszczenia");
                latestPositionFromDb.setEndDate(Converters.longToString(newGeo.getDate()));
                latestPositionFromDb.setStatus("Postój");
            }

            latestPositionFromDb.setLastLocationDate(newGeo.getDate());
            updatePosition(latestPositionFromDb);

        } else if (latestPositionFromDb.getStatus().

                equals("Postój"))

        {
            Timber.d("status geo postój");

            latestPositionFromDb.setEndDate(Converters.longToString(newGeo.getDate()));
            latestPositionFromDb.setLastLocationDate(newGeo.getDate());
            updatePosition(latestPositionFromDb);

            if (isLastGeoFarAway()) {
                Timber.d("bylo przemieszczenie");
                assignGeoToPosition(new PositionGeoJoin(latestPositionFromDb.getId(), newGeo.getId()));

                newPosition = new Position();
                newPosition.setStatus("Ruch");
                newPosition.setStartLocation(locationAddress);
                newPosition.setLastLocationDate(newGeo.getDate() + NEW_POSITION_OFFSET);
                newPosition.setStartDate(Converters.longToString(newGeo.getDate()));
                sendPosition(newPosition);
            }

        } else if (latestPositionFromDb.getStatus().

                equals("Ruch"))

        {
            Timber.d("status geo ruch");

            latestPositionFromDb.setLastLocationDate(newGeo.getDate());
            latestPositionFromDb.setEndDate(Converters.longToString(newGeo.getDate()));
            latestPositionFromDb.setEndLocation(locationAddress);
            updatePosition(latestPositionFromDb);

            if (isLastGeoFarAway()) {
                Timber.d("bylo przemieszczenie");

            } else {
                Timber.d("nie bylo przemieszczenia");
                assignGeoToPosition(new PositionGeoJoin(latestPositionFromDb.getId(), newGeo.getId()));

                newPosition = new Position();
                newPosition.setStatus("Postój");
                newPosition.setStartLocation(locationAddress);
                newPosition.setStartDate(Converters.longToString(newGeo.getDate()));
                newPosition.setLastLocationDate(newGeo.getDate() + NEW_POSITION_OFFSET);
                sendPosition(newPosition);
            }

        } else

        {
            Timber.d("Zaden status sie nie zgadzal");
            throw new RuntimeException("Wrong position status");
        }
        if (newPosition != null)

        {
            PositionGeoJoin positionGeoJoin = new PositionGeoJoin(newPosition.getId(), newGeo.getId());
            Timber.d("assignGeoToPosition = " + positionGeoJoin.toString());
            assignGeoToPosition(positionGeoJoin);
        }

//        onDestroy();

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

    public void getLocationAddress(Location location) {
        newGeo = new Geo(location, location.getTime());

        Timber.d("getLocationAddress");
        Intent intent = new Intent(context, GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        context.startService(intent);
    }

    private void sendPosition(Position newPosition) {
        repository.postPosition(newPosition);
    }


    private boolean isLatestGeoFromDbTooOld() {
        return (System.currentTimeMillis() - latestGeoFromDb.getDate()) > 10800000;
    }

    private void sendGeo(Geo newGeo) {
        repository.postGeo(newGeo);
    }


    private void getOldestGeoForPositionFromDb() {
        repository.getOldestGeoForPosition(latestPositionFromDb.getId());
    }


    private void getLastGeoFromDb() {
        repository.getLatestGeo();
    }

    @Override
    public void setLatestGeoFromDb(Geo geo) {
        latestGeoFromDb = geo;
        setLocationPosition();
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
            locationAddress = resultData.getString(Constants.RESULT_DATA_KEY);
            getLastPositionFromDb();
        }
    }


}