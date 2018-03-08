package com.example.asinit_user.mvvmapplication.db;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.example.asinit_user.mvvmapplication.AppExecutors;
import com.example.asinit_user.mvvmapplication.db.dao.ActionDao;
import com.example.asinit_user.mvvmapplication.db.dao.GeoDao;
import com.example.asinit_user.mvvmapplication.db.entities.ActionEntity;
import com.example.asinit_user.mvvmapplication.db.entities.Geo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class Repository {

    private MediatorLiveData<ActionEntity> observableAction;
    private MediatorLiveData<List<ActionEntity>> observableActions;
    private MediatorLiveData<List<Geo>> observableGeos;

    private ActionDao actionDao;
    private GeoDao geoDao;

    public Repository(ActionDao actionDao, GeoDao geoDao) {
        this.actionDao = actionDao;
        this.geoDao = geoDao;
        initActions();

        observableActions = new MediatorLiveData<>();
        observableActions.addSource(actionDao.loadActions(),
                actions -> observableActions.postValue(actions));

        observableAction = new MediatorLiveData<>();
        observableAction.addSource(actionDao.loadAction(0),
                action -> observableAction.postValue(action));

        observableGeos = new MediatorLiveData<>();
        observableGeos.addSource(geoDao.loadGeos(),
                geos -> observableGeos.postValue(geos));


    }
    public LiveData<ActionEntity> getAction() {
        Log.d("REPOSITORY", "get action " + observableAction + " from the database");
        return observableAction;
    }

    public LiveData<List<ActionEntity>> getActions() {
        return observableActions;
    }

    public LiveData<List<Geo>> getGeos() {
        return observableGeos;
    }

    public void postAction(ActionEntity actionEntity){
        Timber.d("writing action " + actionEntity.toString() + " to the database");
        actionDao.insertAction(actionEntity);
    }

    public void postActions(List<ActionEntity> actionEntities){
        actionDao.insertAll(actionEntities);
    }

    public void postGeo(Geo geo){
        Timber.d("dodajemy nowe geo = " + geo);
//        new AddGeo().execute(geo);
        geoDao.insertGeo(geo);
    }



    public void initActions() {
        List<ActionEntity> actionEntityList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            actionEntityList.add(new ActionEntity(i, "akcja numer " + i));
        }
        new AddItemsTask().execute(actionEntityList);
    }


    private class AddItemsTask extends AsyncTask<List<ActionEntity>, Void, Void> {

        @Override
        protected Void doInBackground(List<ActionEntity>... list) {
            postActions(list[0]);
            return null;
        }
    }

    private class AddGeo extends AsyncTask<Geo, Void, Void> {

        @Override
        protected Void doInBackground(Geo... geo) {
            geoDao.insertGeo(geo[0]);
            return null;
        }
    }
}
