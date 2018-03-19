package com.example.asinit_user.gdziejestczoper.ui.map;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;

import com.example.asinit_user.gdziejestczoper.db.Repository;
import com.example.asinit_user.gdziejestczoper.db.entities.Geo;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

public class MapViewModel extends ViewModel {

    private Repository repository;
    public ObservableField<LatLng> mMapLatLng = new ObservableField<>();
    private final MediatorLiveData<Geo> mObservableGeo;


    @Inject
    public MapViewModel(Repository repository) {
        this.repository = repository;

        mObservableGeo = new MediatorLiveData<>();
        mObservableGeo.setValue(null);

        LiveData<Geo> observableGeo = repository.getGeo();

        mObservableGeo.addSource(observableGeo, mObservableGeo::setValue);
    }
}
