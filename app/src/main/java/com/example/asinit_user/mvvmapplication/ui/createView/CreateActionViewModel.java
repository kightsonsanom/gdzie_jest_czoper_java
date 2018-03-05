package com.example.asinit_user.mvvmapplication.ui.createView;


import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import com.example.asinit_user.mvvmapplication.db.Repository;
import com.example.asinit_user.mvvmapplication.db.entities.ActionEntity;

import javax.inject.Inject;

public class CreateActionViewModel extends ViewModel {

    @Inject
    Repository repository;

    @Inject
    public CreateActionViewModel (){
    }

    public void postAction (ActionEntity actionEntity){
        new AddItemTask().execute(actionEntity);
    }

    private class AddItemTask extends AsyncTask<ActionEntity, Void, Void> {

        @Override
        protected Void doInBackground(ActionEntity... item) {
            repository.postAction(item[0]);
            return null;
        }
    }

}
