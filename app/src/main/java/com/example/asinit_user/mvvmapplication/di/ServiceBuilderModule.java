package com.example.asinit_user.mvvmapplication.di;

import com.example.asinit_user.mvvmapplication.services.GeoService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ServiceBuilderModule {

    @ContributesAndroidInjector
    abstract GeoService contributeGeoService();
}
