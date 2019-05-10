package com.example.asinit_user.gdziejestczoper.di;

//import com.example.asinit_user.gdziejestczoper.services.GeoJobIntentService;
import com.example.asinit_user.gdziejestczoper.di.scopes.GeocodeScope;
import com.example.asinit_user.gdziejestczoper.di.scopes.ServiceScope;
import com.example.asinit_user.gdziejestczoper.services.GeoJobIntentService;
import com.example.asinit_user.gdziejestczoper.services.GeocodeAddressIntentService;
import com.example.asinit_user.gdziejestczoper.services.LogJobIntentService;
//import com.example.asinit_user.gdziejestczoper.services.GeoService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ServiceBuilderModule {

    @ContributesAndroidInjector
    abstract GeoJobIntentService contributeGeoService();

    @ContributesAndroidInjector
    abstract GeocodeAddressIntentService contributeGeocodeService();


    @ContributesAndroidInjector
    abstract LogJobIntentService contributeLogService();
}
