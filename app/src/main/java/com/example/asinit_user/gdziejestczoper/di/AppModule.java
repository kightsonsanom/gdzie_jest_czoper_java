package com.example.asinit_user.gdziejestczoper.di;


import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.asinit_user.gdziejestczoper.AppExecutors;
import com.example.asinit_user.gdziejestczoper.api.CzoperApi;
import com.example.asinit_user.gdziejestczoper.db.AppDatabase;
import com.example.asinit_user.gdziejestczoper.db.Repository;
import com.example.asinit_user.gdziejestczoper.db.dao.PositionDao;
import com.example.asinit_user.gdziejestczoper.db.dao.GeoDao;
import com.example.asinit_user.gdziejestczoper.db.dao.PositionGeoJoinDao;
import com.example.asinit_user.gdziejestczoper.utils.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


@Module(includes = ViewModelModule.class)
public class AppModule {


    @Provides
    @Singleton
    CzoperApi provideCzoperApi() {
        return new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(CzoperApi.class);
    }


    @Provides
    @Singleton
    PositionDao providePositionDao(AppDatabase database){
        return database.positionDao();
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

//    @Provides
//    @Singleton
//    Repository provideRepository(GeoDao geoDao, PositionDao positionDao, PositionGeoJoinDao positionGeoJoinDao, AppExecutors appExecutors){
//        return new Repository(positionDao, geoDao, positionGeoJoinDao, appExecutors);
//    }

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
