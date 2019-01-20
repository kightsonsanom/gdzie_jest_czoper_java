package com.example.asinit_user.gdziejestczoper.di;

import android.app.Application;

import com.example.asinit_user.gdziejestczoper.App;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {AndroidInjectionModule.class, AppModule.class, ServiceBuilderModule.class, ActivityBuilderModule.class})
public interface AppComponent {

//  Component.Builder is a custom builder for AppComponent. We provide BindsInstance for the application.
//  That's how AppModule knows to get application without @Provides @Singleton. We use custom builder so
//  we don't need to pass objects via the AppModule constructor.

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    void inject(App app);
}