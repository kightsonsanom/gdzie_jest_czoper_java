package com.example.asinit_user.gdziejestczoper.di;


import com.example.asinit_user.gdziejestczoper.ui.mainView.GeoAdapter;
import com.example.asinit_user.gdziejestczoper.ui.mainView.PositionsAdapter;

import dagger.Module;
import dagger.Provides;

@Module
class PositionListFragmentModule {

    @Provides
    @PositionListFragmentScope
    PositionsAdapter providePositionsAdapter (){
        return new PositionsAdapter();
    }

    @Provides
    @PositionListFragmentScope
    GeoAdapter provideGeoAdapter (){
        return new GeoAdapter();
    }
}