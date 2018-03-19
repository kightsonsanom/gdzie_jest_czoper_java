package com.example.asinit_user.gdziejestczoper.di;


import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.asinit_user.gdziejestczoper.db.AppDatabase;
import com.example.asinit_user.gdziejestczoper.db.dao.PositionDao;
import com.example.asinit_user.gdziejestczoper.db.dao.GeoDao;
import com.example.asinit_user.gdziejestczoper.db.dao.PositionGeoJoinDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module(includes = ViewModelModule.class)
public class AppModule {


    @Provides
    @Singleton
    PositionDao provideActionDao(AppDatabase database){
        return database.actionDao();
    }

    @Provides
    @Singleton
    GeoDao provideGeoDao(AppDatabase database){
        return database.geoDao();
    }

    @Provides
    @Singleton
    PositionGeoJoinDao providePositionGeoJoinDao(AppDatabase database){
        return database.positionGeoJoinDao();
    }

    @Provides
    @Singleton
    AppDatabase provideDatabase(Application application){
        return Room.databaseBuilder(
                application,
                AppDatabase.class,
                "actions.db"
        ).build();
    }
}
