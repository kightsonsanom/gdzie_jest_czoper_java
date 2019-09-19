package pl.tolichwer.gdziejestczoper.di;

import android.app.Application;

import pl.tolichwer.gdziejestczoper.App;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class, AppModule.class, ServiceBuilderModule.class, ActivityBuilderModule.class})
public interface AppComponent extends AndroidInjector<App> {

    @Component.Factory
    interface Factory {
        AppComponent create(@BindsInstance Application application);
    }
}