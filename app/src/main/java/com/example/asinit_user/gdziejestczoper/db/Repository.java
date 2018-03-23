package com.example.asinit_user.gdziejestczoper.db;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.asinit_user.gdziejestczoper.AppExecutors;
import com.example.asinit_user.gdziejestczoper.api.ApiResponse;
import com.example.asinit_user.gdziejestczoper.api.CzoperApi;
import com.example.asinit_user.gdziejestczoper.api.NetworkBoundResource;
import com.example.asinit_user.gdziejestczoper.db.dao.PositionDao;
import com.example.asinit_user.gdziejestczoper.db.dao.GeoDao;
import com.example.asinit_user.gdziejestczoper.db.dao.PositionGeoJoinDao;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;
import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.PositionGeoJoin;
import com.example.asinit_user.gdziejestczoper.services.PositionManagerCallback;
import com.example.asinit_user.gdziejestczoper.ui.search.SearchFragmentViewModelCallback;
import com.example.asinit_user.gdziejestczoper.viewobjects.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class Repository {

    private PositionManagerCallback positionManagerCallback;
    private SearchFragmentViewModelCallback searchFragmentViewModelCallback;

    private MediatorLiveData<List<Position>> observablePositions;
    private MediatorLiveData<List<Geo>> observableGeos;
    private MediatorLiveData<Geo> observableGeo;

    private PositionDao positionDao;
    private GeoDao geoDao;
    private PositionGeoJoinDao positionGeoJoinDao;
    private CzoperApi czoperApi;
    private AppExecutors appExecutors;


    private Geo latestGeoFromDb;
    private Position latestPositionFromDb;
    private List<Geo> allGeos;
    private List<Position> allPositions;

    @Inject
    public Repository(PositionDao positionDao, GeoDao geoDao, PositionGeoJoinDao positionGeoJoinDao, AppExecutors appExecutors, CzoperApi czoperApi) {
        this.appExecutors = appExecutors;
        this.positionDao = positionDao;
        this.positionGeoJoinDao = positionGeoJoinDao;
        this.geoDao = geoDao;
        this.czoperApi = czoperApi;

        observablePositions = new MediatorLiveData<>();
        observablePositions.addSource(positionDao.loadPositions(),
                positions -> observablePositions.postValue(positions));

        observableGeos = new MediatorLiveData<>();
        observableGeos.addSource(geoDao.loadGeos(),
                geos -> observableGeos.postValue(geos));


        observableGeo = new MediatorLiveData<>();
        observableGeo.addSource(geoDao.loadLatestLiveDataGeo(),
                geo -> observableGeo.postValue(geo));

    }

    public void setPositionManagerCallback(PositionManagerCallback positionManagerCallback) {
        this.positionManagerCallback = positionManagerCallback;
    }

    public void setSearchFragmentViewModelCallback(SearchFragmentViewModelCallback searchFragmentViewModelCallback) {
        this.searchFragmentViewModelCallback = searchFragmentViewModelCallback;
    }


//    public LiveData<List<Position>> getPositions() {
//        return observablePositions;
//    }

    public LiveData<Resource<List<Position>>> getPositions() {
        return new NetworkBoundResource<List<Position>, List<Position>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<Position> item) {
                positionDao.insertAll(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Position> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<Position>> loadFromDb() {
                return positionDao.loadPositions();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Position>>> createCall() {
                return czoperApi.getAllPositions();
            }
        }.asLiveData();
    }

    public LiveData<List<Geo>> getGeos() {
        return observableGeos;
    }

    public LiveData<Geo> getGeo() {
        Timber.d("getting latest geo");
        return observableGeo;
    }

    public void postPosition(Position position) {
        appExecutors.diskIO().execute(() -> {
            Timber.d("inserting position into DB ID: " + position.getId() + " czas: " + position.getLastLocationDate());
            positionDao.insertPosition(position);
        });
    }


    public void postGeo(Geo geo) {

        appExecutors.diskIO().execute(() -> {
            Timber.d("inserting geo into DB ID: " + geo.getId() + " czas: " + geo.getDate());
            geoDao.insertGeo(geo);
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

    public void getLatestGeoForTests() {
        appExecutors.diskIO().execute(() -> {
            Geo latestGeo = geoDao.loadLatestGeo();

            searchFragmentViewModelCallback.setLatestGeo(latestGeo);

        });
    }

    public List<Position> getAllPositions() {
        appExecutors.diskIO().execute(() -> {
            allPositions = positionDao.getAllPositions();
        });
        return allPositions;
    }

    public List<Geo> getAllGeos() {
        appExecutors.diskIO().execute(() -> {
            allGeos = geoDao.getAllGeos();
        });
        return allGeos;
    }

    public void getPositionsFromRange(String searchFromDay, String searchToDay) {
        Timber.d("getPositionsFromRange method searchFromDay = " + searchFromDay + " searchToDay = " + searchToDay);
        appExecutors.diskIO().execute(()-> {
                List<Position> positions = positionDao.getPositionsFromRange(searchFromDay, searchToDay);
                LiveData<List<Position>> livePositions = positionDao.getLivePositionsFromRange(searchFromDay, searchToDay);

                Timber.d("positions = " + positions);
                Timber.d("livePositions = " + livePositions.getValue());

                searchFragmentViewModelCallback.setObservablePositions(livePositions);

        });
    }
}
