package com.example.asinit_user.mvvmapplication.di;


import android.app.Application;
import android.arch.persistence.room.Room;

import com.example.asinit_user.mvvmapplication.App;
import com.example.asinit_user.mvvmapplication.db.AppDatabase;
import com.example.asinit_user.mvvmapplication.db.Repository;
import com.example.asinit_user.mvvmapplication.db.dao.ActionDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module(includes = ViewModelModule.class)
public class AppModule {


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
        return Room.databaseBuilder(
                application,
                AppDatabase.class,
                "actions.db"
        ).build();
    }
}
