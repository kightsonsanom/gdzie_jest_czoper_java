package com.example.asinit_user.mvvmapplication.db;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.example.asinit_user.mvvmapplication.AppExecutors;
import com.example.asinit_user.mvvmapplication.db.dao.PositionDao;
import com.example.asinit_user.mvvmapplication.db.dao.GeoDao;
import com.example.asinit_user.mvvmapplication.db.dao.PositionGeoJoinDao;
import com.example.asinit_user.mvvmapplication.db.entities.Position;
import com.example.asinit_user.mvvmapplication.db.entities.Geo;
import com.example.asinit_user.mvvmapplication.db.entities.PositionGeoJoin;
import com.example.asinit_user.mvvmapplication.services.PositionManagerCallback;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class Repository {

    PositionManagerCallback positionManagerCallback;
    private MediatorLiveData<List<Position>> observablePositions;
    private MediatorLiveData<List<Geo>> observableGeos;
//    private MediatorLiveData<Geo> observableGeo;

    private PositionDao positionDao;
    private GeoDao geoDao;
    private AppExecutors appExecutors;
    private PositionGeoJoinDao positionGeoJoinDao;
    private Geo latestGeoFromDb;
    private Position latestPositionFromDb;
    private List<Geo> allGeos;

    @Inject
    public Repository(PositionDao positionDao, GeoDao geoDao, PositionGeoJoinDao positionGeoJoinDao, AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        this.positionDao = positionDao;
        this.positionGeoJoinDao = positionGeoJoinDao;
        this.geoDao = geoDao;

        observablePositions = new MediatorLiveData<>();
        observablePositions.addSource(positionDao.loadPositions(),
                positions -> observablePositions.postValue(positions));

        observableGeos = new MediatorLiveData<>();
        observableGeos.addSource(geoDao.loadGeos(),
                geos -> observableGeos.postValue(geos));


//        observableGeo = new MediatorLiveData<>();
//        observableGeo.addSource(geoDao.loadLatestLiveDataGeo(),
//                geo -> observableGeo.postValue(geo));

    }

    public void setPositionManagerCallback(PositionManagerCallback positionManagerCallback) {
        this.positionManagerCallback = positionManagerCallback;
    }

    public LiveData<List<Position>> getPositions() {
        return observablePositions;
    }

    public LiveData<List<Geo>> getGeos() {
        return observableGeos;
    }

    public void postPosition(Position position) {
        appExecutors.diskIO().execute(() -> {
            Timber.d("inserting position into DB ID: " + position.getId() + " czas: " + position.getLastLocationDate());
            positionDao.insertPosition(position);
        });
    }

    private void postPositions(List<Position> positionList) {
        appExecutors.diskIO().execute(() -> positionDao.insertAll(positionList));
    }

    public void postGeo(Geo geo) {

        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Timber.d("inserting geo into DB ID: " + geo.getId() + " czas: " + geo.getDate());
                geoDao.insertGeo(geo);
            }
        });
    }

    public void getLatestGeo() {
        appExecutors.diskIO().execute(() -> {
            latestGeoFromDb = geoDao.loadLatestGeo();
            if (latestGeoFromDb != null) {
                Timber.d("latestGeoFromDb ID = " + latestGeoFromDb.getId() + " czas: " + latestGeoFromDb.getDate());
            } else {
                Timber.d("latestGeoFromDb is null");
            }
            positionManagerCallback.setLatestGeoFromDb(latestGeoFromDb);
        });
    }

    public void getLatestPosition() {
            appExecutors.diskIO().execute(() -> {
                latestPositionFromDb = positionDao.loadLatestPosition();
                if (latestPositionFromDb != null) {
                    Timber.d("latestPositionFromDb ID = " + latestPositionFromDb.getId() + " czas: " + latestPositionFromDb.getLastLocationDate());
                } else {
                    Timber.d("latestPositionFromDb is null");
                }
                positionManagerCallback.setLatestPositionFromDb(latestPositionFromDb);
            });
    }

    public void getOldestGeoForPosition(String positionID) {
        appExecutors.diskIO().execute(() -> {
            latestGeoFromDb = positionGeoJoinDao.getOldestGeoForPosition(positionID);
            if (latestGeoFromDb != null) {
                Timber.d("latestGeoFromDb ID = " + latestGeoFromDb.getId() + " czas: " + latestGeoFromDb.getDate());
            } else {
                Timber.d("latestGeoFromDb is null");
            }
            positionManagerCallback.setLatestGeoFromDb(latestGeoFromDb);
        });
    }
    public void assignGeoToPosition(PositionGeoJoin positionGeoJoin) {
        appExecutors.diskIO().execute(() -> positionGeoJoinDao.insert(positionGeoJoin));
    }

    public void updatePosition(Position position) {
        Timber.d("updating position ID = " + position.getId() + " czas: " + position.getLastLocationDate() + " status = " + position.getStatus());
        appExecutors.diskIO().execute(() -> positionDao.updatePosition(position));

    }
}
