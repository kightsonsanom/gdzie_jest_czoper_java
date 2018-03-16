package com.example.asinit_user.mvvmapplication.di;

import com.example.asinit_user.mvvmapplication.ui.mainView.NavigationActivity;
import com.example.asinit_user.mvvmapplication.ui.mainView.PositionsAdapter;
import com.example.asinit_user.mvvmapplication.ui.mainView.GeoAdapter;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class NavigationActivityModule {

   @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract NavigationActivity contributeNavigationActivity();
}
