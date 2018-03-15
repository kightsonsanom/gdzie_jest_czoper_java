package com.example.asinit_user.mvvmapplication.di;

import com.example.asinit_user.mvvmapplication.services.GeoService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ServiceBuilderModule {

    @ServiceScope
    @ContributesAndroidInjector(modules = GeoServiceModule.class)
    abstract GeoService contributeGeoService();
}
