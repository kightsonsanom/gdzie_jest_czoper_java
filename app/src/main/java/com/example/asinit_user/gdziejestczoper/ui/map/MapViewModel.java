package com.example.asinit_user.gdziejestczoper.ui.map;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;

import com.example.asinit_user.gdziejestczoper.db.Repository;
import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.Resource;

import java.util.List;

import javax.inject.Inject;

public class MapViewModel extends ViewModel {


    public ObservableField<List<Geo>> latestGeoList = new ObservableField<>();
    private final MediatorLiveData<Resource<List<Geo>>> mObservableGeos;
    public int userID;

    @Inject
    public MapViewModel(Repository repository) {

        mObservableGeos = new MediatorLiveData<>();
        mObservableGeos.setValue(null);

        LiveData<Resource<List<Geo>>> observableGeo = repository.getLatestGeoForUsers();
        mObservableGeos.addSource(observableGeo, value -> mObservableGeos.setValue(value));

        userID = repository.getUserID();
    }

    public LiveData<Resource<List<Geo>>> getObservableGeo() {
        return mObservableGeos;
    }

    public void setMapGeos(List<Geo> mapGeoList) {
        this.latestGeoList.set(mapGeoList);
    }

}
