package com.example.asinit_user.gdziejestczoper.ui.map;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;

import com.example.asinit_user.gdziejestczoper.db.Repository;
import com.example.asinit_user.gdziejestczoper.viewobjects.MapGeo;
import com.example.asinit_user.gdziejestczoper.viewobjects.Resource;

import java.util.List;

import javax.inject.Inject;

public class MapViewModel extends ViewModel {


    private Repository repository;
    public ObservableField<List<MapGeo>> mapGeoList= new ObservableField<>();
    private final MediatorLiveData<Resource<List<MapGeo>>> mObservableGeo;

    @Inject
    public MapViewModel(Repository repository) {
        this.repository = repository;
        mObservableGeo = new MediatorLiveData<>();
        mObservableGeo.setValue(null);

        LiveData<Resource<List<MapGeo>>> observableGeo = repository.getMapGeos();
        mObservableGeo.addSource(observableGeo, value -> mObservableGeo.setValue(value));
    }

    public LiveData<Resource<List<MapGeo>>> getObservableGeo() {
        return mObservableGeo;
    }

    public void setMapGeos(List<MapGeo> mapGeoList) {
        this.mapGeoList.set(mapGeoList);
    }
}
