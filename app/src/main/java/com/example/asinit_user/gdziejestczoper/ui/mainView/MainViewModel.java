package com.example.asinit_user.gdziejestczoper.ui.mainView;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;

import com.example.asinit_user.gdziejestczoper.db.Repository;
import com.example.asinit_user.gdziejestczoper.db.entities.Position;
import com.example.asinit_user.gdziejestczoper.db.entities.Geo;

import java.util.List;

import javax.inject.Inject;

public class MainViewModel extends ViewModel {

    //pozycje, które obserwujemy na bazie
    private final MediatorLiveData<List<Position>> mObservablePositions;
    private final MediatorLiveData<List<Geo>> mObservableGeos;
//    private final MediatorLiveData<Geo> mObservableGeo;

    // pozycja obserwowana z widoku
    public ObservableField<Geo> geo= new ObservableField<>();

    Repository repository;

    @Inject
    public MainViewModel(Repository repository){
        this.repository = repository;
        mObservablePositions = new MediatorLiveData<>();
        mObservableGeos = new MediatorLiveData<>();
//        mObservableGeo = new MediatorLiveData<>();
        mObservablePositions.setValue(null);
        mObservableGeos.setValue(null);
//        mObservableGeo.setValue(null);


        //obserwujemy repozytorium i jeśli ulegnie zmianie to robimy update
        LiveData<List<Position>> observablePositions= repository.getPositions();
        LiveData<List<Geo>> observableGeos = repository.getGeos();
//        LiveData<Geo> observableGeo = repository.getGeo();
        mObservableGeos.addSource(observableGeos, mObservableGeos::setValue);
        mObservablePositions.addSource(observablePositions, mObservablePositions::setValue);
//        mObservableGeo.addSource(observableGeo, mObservableGeo::setValue);
    }

    public LiveData<List<Position>> getObservablePositions() {
        return mObservablePositions;
    }

    public LiveData<List<Geo>> getObservableGeos() {
        return mObservableGeos;
    }

//    public LiveData<Geo> getObservableGeo() {
//        return mObservableGeo;
//    }


    public void setGeo(Geo geo) {
        this.geo.set(geo);
    }

//
//    public String getLatestGeo() {
//        Geo geo = repository.getLatestGeo();
//        if (geo == null){
//            return "geo is null";
//        } else {
//            return geo.getDisplayText();
//        }
//    }
}
