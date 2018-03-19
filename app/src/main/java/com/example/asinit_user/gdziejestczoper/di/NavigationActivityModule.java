package com.example.asinit_user.gdziejestczoper.di;

import com.example.asinit_user.gdziejestczoper.ui.mainView.NavigationActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class NavigationActivityModule {

   @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract NavigationActivity contributeNavigationActivity();
}
