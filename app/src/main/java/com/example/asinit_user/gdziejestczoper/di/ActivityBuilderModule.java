package com.example.asinit_user.gdziejestczoper.di;

import com.example.asinit_user.gdziejestczoper.ui.login.LoginActivity;
import com.example.asinit_user.gdziejestczoper.ui.mainView.NavigationActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract NavigationActivity contributeNavigationActivity();
}
