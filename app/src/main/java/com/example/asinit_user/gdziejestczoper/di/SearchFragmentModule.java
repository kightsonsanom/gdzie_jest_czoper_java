package com.example.asinit_user.gdziejestczoper.di;


import com.example.asinit_user.gdziejestczoper.ui.geoList.PositionsAdapter;
import com.example.asinit_user.gdziejestczoper.ui.mainView.GeoAdapter;

import dagger.Module;
import dagger.Provides;

@Module
class SearchFragmentModule {

    @Provides
    @SearchFragmentScope
    PositionsAdapter providePositionsAdapter (){
        return new PositionsAdapter();
    }

    @Provides
    @PositionListFragmentScope
    GeoAdapter provideGeoAdapter (){
        return new GeoAdapter();
    }
}
