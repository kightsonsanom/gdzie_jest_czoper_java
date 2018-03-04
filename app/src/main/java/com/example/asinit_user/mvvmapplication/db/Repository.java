package com.example.asinit_user.mvvmapplication.db;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.example.asinit_user.mvvmapplication.db.dao.ActionDao;
import com.example.asinit_user.mvvmapplication.db.entities.ActionEntity;

import java.util.List;

import javax.inject.Inject;

public class Repository {

    private MediatorLiveData<ActionEntity> observableAction;
    private MediatorLiveData<List<ActionEntity>> observableActions;
    private ActionDao actionDao;

    @Inject
    public Repository(ActionDao actionDao) {
        this.actionDao = actionDao;
        observableActions = new MediatorLiveData<>();
        observableActions.addSource(actionDao.loadActions(),
                actions -> observableActions.postValue(actions));
    }
    public LiveData<ActionEntity> getAction() {
        return observableAction;
    }

    public LiveData<List<ActionEntity>> getActions() {
        return observableActions;
    }

    public void postAction(ActionEntity actionEntity){
        actionDao.insertAction(actionEntity);
    }

    public void postActions(List<ActionEntity> actionEntities){
        actionDao.insertAll(actionEntities);
    }
}
