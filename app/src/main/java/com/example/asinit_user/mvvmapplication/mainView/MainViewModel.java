package com.example.asinit_user.mvvmapplication.mainView;


import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import com.example.asinit_user.mvvmapplication.App;
import com.example.asinit_user.mvvmapplication.db.Repository;
import com.example.asinit_user.mvvmapplication.db.entities.ActionEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainViewModel extends ViewModel {

    private final MediatorLiveData<List<ActionEntity>> mObservableActions;

    @Inject
    Repository repository;

    @Inject
    public MainViewModel(){
        mObservableActions = new MediatorLiveData<>();
        mObservableActions.setValue(null);

        LiveData<List<ActionEntity>> observableActions = repository.getActions();

        mObservableActions.addSource(observableActions, mObservableActions::setValue);
    }

    public LiveData<List<ActionEntity>> getActions() {
        return mObservableActions;
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
            repository.postActions(list[0]);
            return null;
        }
    }
}
