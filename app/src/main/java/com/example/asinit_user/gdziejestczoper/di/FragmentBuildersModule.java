package com.example.asinit_user.gdziejestczoper.di;


import com.example.asinit_user.gdziejestczoper.di.scopes.PositionListFragmentScope;
import com.example.asinit_user.gdziejestczoper.di.scopes.SearchFragmentScope;
import com.example.asinit_user.gdziejestczoper.ui.geoList.PositionListFragment;
import com.example.asinit_user.gdziejestczoper.ui.map.MapFragment;
import com.example.asinit_user.gdziejestczoper.ui.search.SearchFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract MapFragment contributeMapFragment();

    @SearchFragmentScope
    @ContributesAndroidInjector(modules = SearchFragmentModule.class)
    abstract SearchFragment contributeSearchFragment();

    @PositionListFragmentScope
    @ContributesAndroidInjector(modules = PositionListFragmentModule.class)
    abstract PositionListFragment contributePositionListFragment();
}
