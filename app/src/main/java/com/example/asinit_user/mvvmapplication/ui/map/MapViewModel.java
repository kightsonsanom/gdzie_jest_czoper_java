package com.example.asinit_user.mvvmapplication.ui.map;


import android.arch.lifecycle.ViewModel;

import com.example.asinit_user.mvvmapplication.db.Repository;

import javax.inject.Inject;

public class MapViewModel extends ViewModel {


    @Inject
    public MapViewModel(Repository repository)
    {

    }
}
