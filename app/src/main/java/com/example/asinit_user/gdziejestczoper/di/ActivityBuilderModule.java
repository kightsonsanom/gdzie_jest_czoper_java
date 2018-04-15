package com.example.asinit_user.gdziejestczoper.di;

import com.example.asinit_user.gdziejestczoper.services.GeoJobIntentService;
import com.example.asinit_user.gdziejestczoper.ui.login.LoginActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ActivityBuilderModule {

    @LoginActivityScope
    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

}
