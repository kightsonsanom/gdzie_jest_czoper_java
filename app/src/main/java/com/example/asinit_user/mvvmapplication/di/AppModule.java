package com.example.asinit_user.mvvmapplication.di;


import android.app.Application;

import com.example.asinit_user.mvvmapplication.App;

import dagger.Module;
import dagger.Provides;


@Module
public class AppModule {

    private final App application;
    public AppModule(App application) {
        this.application = application;
    }

    @Provides
    Application provideApplication(){
        return application;
    }
}
