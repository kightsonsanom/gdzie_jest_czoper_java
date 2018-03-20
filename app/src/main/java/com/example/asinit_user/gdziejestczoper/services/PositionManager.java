package com.example.asinit_user.gdziejestczoper.services;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.example.asinit_user.gdziejestczoper.db.Repository;
import com.example.asinit_user.gdziejestczoper.db.entities.Geo;
import com.example.asinit_user.gdziejestczoper.db.entities.Position;
import com.example.asinit_user.gdziejestczoper.db.entities.PositionGeoJoin;
import com.example.asinit_user.gdziejestczoper.utils.Constants;
import com.example.asinit_user.gdziejestczoper.utils.Converters;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import timber.log.Timber;

public class PositionManager implements PositionManagerCallback {


    private final static float ACCEPTABLE_DISTANCE_BETWEEN_GEO = 125f;
    // kiedy konczy sie ruch i zaczyna postoj to postoj musi byc bardziej aktualny od ruchu
    private static final long NEW_POSITION_OFFSET = 1;
    Repository repository;

    private Context context;
    private AddressResultReceiver addressResultReceiver = new AddressResultReceiver(new Handler());
    private String locationAddress;

    Geo newGeo;
    Geo latestGeoFromDb;
    Position newPosition;
    Position latestPositionFromDb;


    @Inject
    public PositionManager(Context context, Repository repository) {
        this.context = context;
        this.repository = repository;
        repository.setPositionManagerCallback(this);

    }

    public void setLocationPosition() {
        sendGeo(newGeo);

        if (latestGeoFromDb == null || isLatestGeoFromDbTooOld()) {
            Timber.d("latestPositionFromDb is null lub geo za stare");

            if (isLatestGeoFromDbTooOld()) {
                Timber.d("Geo za stare");
                newPosition = new Position();
                newPosition.setStartDate(Converters.longToString(latestGeoFromDb.getDate()));
                newPosition.setEndDate(Converters.longToString(newGeo.getDate()));
                newPosition.setStatus("Przerwa");

                sendPosition(newPosition);
            }

            newPosition = new Position();
            newPosition.setStartLocation(locationAddress);
            newPosition.setStartDate(Converters.longToString(newGeo.getDate()));
            newPosition.setStatus("Nieznany");
            newPosition.setLastLocationDate(newGeo.getDate());

            sendPosition(newPosition);

        } else if (latestPositionFromDb.getStatus().equals("Nieznany")) {
            Timber.d("status geo nieznany");

            if (isLastGeoFarAway()) {
                Timber.d("bylo przemieszczenie");
                latestPositionFromDb.setStatus("Ruch");
                latestPositionFromDb.setLastLocationDate(newGeo.getDate());

            } else {
                Timber.d("nie bylo przemieszczenia");
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
                assignGeoToPosition(new PositionGeoJoin(latestPositionFromDb.getId(), newGeo.getId()));

                newPosition = new Position();
                newPosition.setStatus("Ruch");
                newPosition.setStartLocation(locationAddress);
                newPosition.setLastLocationDate(newGeo.getDate() + NEW_POSITION_OFFSET);
                newPosition.setStartDate(Converters.longToString(newGeo.getDate()));
                sendPosition(newPosition);
            }

        } else if (latestPositionFromDb.getStatus().equals("Ruch")) {
            Timber.d("status geo ruch");

            latestPositionFromDb.setLastLocationDate(newGeo.getDate());
            latestPositionFromDb.setEndDate(Converters.longToString(newGeo.getDate()));
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

        } else {
            Timber.d("Zaden status sie nie zgadzal");
            throw new RuntimeException("Wrong position status");
        }
        if (newPosition != null) {
            PositionGeoJoin positionGeoJoin = new PositionGeoJoin(newPosition.getId(), newGeo.getId());
            Timber.d("assignGeoToPosition = " + positionGeoJoin.toString());
            assignGeoToPosition(positionGeoJoin);
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
        return  distance > ACCEPTABLE_DISTANCE_BETWEEN_GEO;
    }

    public void getLocationAddress(Location location) {
        newGeo = new Geo(location, location.getTime());

        Intent intent = new Intent(context, GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        context.startService(intent);
    }

    private void sendPosition(Position newPosition) {
        repository.postPosition(newPosition);
    }



    private boolean isLatestGeoFromDbTooOld() {
        return (System.currentTimeMillis() - newGeo.getDate()) > 3600000;
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
        repository.getLatestPosition();
    }

    @Override
    public void setLatestPositionFromDb(Position position) {

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
