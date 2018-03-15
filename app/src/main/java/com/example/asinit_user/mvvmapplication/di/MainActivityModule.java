package com.example.asinit_user.mvvmapplication.di;

import com.example.asinit_user.mvvmapplication.ui.mainView.PositionsAdapter;
import com.example.asinit_user.mvvmapplication.ui.mainView.GeoAdapter;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {

    @Provides
    @ActivityScope
    PositionsAdapter providePositionsAdapter (){
        return new PositionsAdapter();
    }

    @Provides
    @ActivityScope
    GeoAdapter provideGeoAdapter (){
        return new GeoAdapter();
    }
}
