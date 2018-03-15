package com.example.asinit_user.mvvmapplication.di;

import android.app.Application;
import android.content.Context;

import com.example.asinit_user.mvvmapplication.db.Repository;
import com.example.asinit_user.mvvmapplication.services.PositionManager;

import dagger.Module;
import dagger.Provides;

@Module
public class GeoServiceModule {

    @Provides
    @ServiceScope
    PositionManager providePositionManager (Application application, Repository repository){
        return new PositionManager(application.getBaseContext(), repository);
    }
}
