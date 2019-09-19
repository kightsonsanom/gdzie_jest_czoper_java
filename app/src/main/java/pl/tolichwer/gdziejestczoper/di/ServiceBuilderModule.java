package pl.tolichwer.gdziejestczoper.di;

import pl.tolichwer.gdziejestczoper.services.GeoJobIntentService;
import pl.tolichwer.gdziejestczoper.services.GeocodeAddressIntentService;
import pl.tolichwer.gdziejestczoper.services.LogJobIntentService;

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
