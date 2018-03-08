package com.example.asinit_user.mvvmapplication.ui.mainView;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.os.AsyncTask;
import android.util.Log;

import com.example.asinit_user.mvvmapplication.db.Repository;
import com.example.asinit_user.mvvmapplication.db.entities.ActionEntity;
import com.example.asinit_user.mvvmapplication.db.entities.Geo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainViewModel extends ViewModel {

    //akcje, które obserwujemy na bazie
    private final MediatorLiveData<List<ActionEntity>> mObservableActions;
    private final MediatorLiveData<ActionEntity> mObservableAction;

    private final MediatorLiveData<List<Geo>> mObservableGeos;

    // akcja obserwowana z widoku
    public ObservableField<ActionEntity> action = new ObservableField<>();

    Repository repository;

    @Inject
    public MainViewModel(Repository repository){
        this.repository = repository;
        mObservableActions = new MediatorLiveData<>();
        mObservableAction = new MediatorLiveData<>();
        mObservableGeos = new MediatorLiveData<>();
        mObservableActions.setValue(null);
        mObservableGeos.setValue(null);
        mObservableAction.setValue(null);

        //obserwujemy repozytorium i jeśli ulegnie zmianie to robimy update
        LiveData<List<ActionEntity>> observableActions = repository.getActions();
        LiveData<ActionEntity> observableAction = repository.getAction();
        LiveData<List<Geo>> observableGeos = repository.getGeos();
        mObservableActions.addSource(observableGeos, mObservableGeos::setValue);
        mObservableActions.addSource(observableActions, mObservableActions::setValue);
        mObservableAction.addSource(observableAction, mObservableAction::setValue);
    }

    public LiveData<List<ActionEntity>> getObservableActions() {
        return mObservableActions;
    }
    public LiveData<List<Geo>> getObservableGeos() {
        return mObservableGeos;
    }


    public LiveData<ActionEntity> getObservableAction() {
        if (mObservableAction ==null){
            Log.d("MAINVIEWMODEL", "mObservableAction is null");
        }
        return mObservableAction;
    }

    public void setAction(ActionEntity action) {
        this.action.set(action);
    }


}
