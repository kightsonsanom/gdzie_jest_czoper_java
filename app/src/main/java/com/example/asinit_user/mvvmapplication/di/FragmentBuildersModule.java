package com.example.asinit_user.mvvmapplication.di;


import com.example.asinit_user.mvvmapplication.ui.geoList.PositionListFragment;
import com.example.asinit_user.mvvmapplication.ui.map.MapFragment;
import com.example.asinit_user.mvvmapplication.ui.search.SearchFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract MapFragment contributeMapFragment();

    @ContributesAndroidInjector
    abstract SearchFragment contributeSearchFragment();

    @PositionListFragmentScope
    @ContributesAndroidInjector(modules = PositionListFragmentModule.class)
    abstract PositionListFragment contributePositionListFragment();
}
