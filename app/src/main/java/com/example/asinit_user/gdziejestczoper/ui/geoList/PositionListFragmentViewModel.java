package com.example.asinit_user.gdziejestczoper.ui.geoList;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;

import com.example.asinit_user.gdziejestczoper.db.Repository;
import com.example.asinit_user.gdziejestczoper.db.entities.Geo;
import com.example.asinit_user.gdziejestczoper.db.entities.Position;

import java.util.List;

import javax.inject.Inject;

public class PositionListFragmentViewModel extends ViewModel {

    //pozycje, które obserwujemy na bazie
    private final MediatorLiveData<List<Position>> mObservablePositions;
    private final MediatorLiveData<List<Geo>> mObservableGeos;


    // pozycja obserwowana z widoku
    public ObservableField<Geo> geo = new ObservableField<>();

    private Repository repository;

    @Inject
    public PositionListFragmentViewModel(Repository repository) {
        this.repository = repository;
        mObservablePositions = new MediatorLiveData<>();
        mObservableGeos = new MediatorLiveData<>();

        mObservablePositions.setValue(null);
        mObservableGeos.setValue(null);



        //obserwujemy repozytorium i jeśli ulegnie zmianie to robimy update
        LiveData<List<Position>> observablePositions = repository.getPositions();
        LiveData<List<Geo>> observableGeos = repository.getGeos();

        mObservableGeos.addSource(observableGeos, mObservableGeos::setValue);
        mObservablePositions.addSource(observablePositions, mObservablePositions::setValue);

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
}