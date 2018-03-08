package com.example.asinit_user.mvvmapplication.di;


import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.asinit_user.mvvmapplication.App;
import com.example.asinit_user.mvvmapplication.db.AppDatabase;
import com.example.asinit_user.mvvmapplication.db.Repository;
import com.example.asinit_user.mvvmapplication.db.dao.ActionDao;
import com.example.asinit_user.mvvmapplication.db.dao.GeoDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module(includes = ViewModelModule.class)
public class AppModule {


    @Provides
    @Singleton
    Repository provideRepository(ActionDao actionDao, GeoDao geoDao){
        return new Repository(actionDao, geoDao);
    }

    @Provides
    @Singleton
    ActionDao provideActionDao(AppDatabase database){
        return database.actionDao();
    }

    @Provides
    @Singleton
    GeoDao provideGeoDao(AppDatabase database){
        return database.geoDao();
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
