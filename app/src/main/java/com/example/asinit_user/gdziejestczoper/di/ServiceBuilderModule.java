package com.example.asinit_user.gdziejestczoper.di;

//import com.example.asinit_user.gdziejestczoper.services.GeoJobIntentService;
import com.example.asinit_user.gdziejestczoper.services.GeoJobIntentService;
//import com.example.asinit_user.gdziejestczoper.services.GeoService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ServiceBuilderModule {

    @ServiceScope
    @ContributesAndroidInjector(modules = GeoServiceModule.class)
    abstract GeoJobIntentService contributeGeoService();
}
