package com.example.asinit_user.mvvmapplication.di;

import com.example.asinit_user.mvvmapplication.ui.mainView.ActionsAdapter;
import com.example.asinit_user.mvvmapplication.ui.mainView.GeoAdapter;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {

    @Provides
    @ActivityScope
    ActionsAdapter provideActionsAdapter (){
        return new ActionsAdapter();
    }

    @Provides
    @ActivityScope
    GeoAdapter provideGeoAdapter (){
        return new GeoAdapter();
    }
}
