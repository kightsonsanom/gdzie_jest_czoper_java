package com.example.asinit_user.mvvmapplication.di;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;

import com.example.asinit_user.mvvmapplication.ViewModelFactory;
import com.example.asinit_user.mvvmapplication.db.AppDatabase;
import com.example.asinit_user.mvvmapplication.db.Repository;
import com.example.asinit_user.mvvmapplication.db.dao.ActionDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {

    private final AppDatabase database;

    public RoomModule(Application application) {
        this.database = Room.databaseBuilder(
                application,
                AppDatabase.class,
                "actions.db"
        ).build();
    }

    @Provides
    @Singleton
    Repository provideListItemRepository(ActionDao actionDao){
        return new Repository(actionDao);
    }

    @Provides
    @Singleton
    ActionDao provideListItemDao(AppDatabase database){
        return database.actionDao();
    }

    @Provides
    @Singleton
    AppDatabase provideDatabase(Application application){
        return database;
    }


//    @Provides
//    @Singleton
//    ViewModelProvider.Factory provideViewModelFactory( ){
//        return new ViewModelFactory();
//    }
}
