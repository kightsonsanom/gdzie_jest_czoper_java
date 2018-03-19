package com.example.asinit_user.gdziejestczoper.di;

import android.app.Application;

import com.example.asinit_user.gdziejestczoper.db.Repository;
import com.example.asinit_user.gdziejestczoper.services.PositionManager;

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
