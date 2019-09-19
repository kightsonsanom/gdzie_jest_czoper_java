package pl.tolichwer.gdziejestczoper.di;


import pl.tolichwer.gdziejestczoper.di.scopes.PositionListFragmentScope;
import pl.tolichwer.gdziejestczoper.di.scopes.SearchFragmentScope;
import pl.tolichwer.gdziejestczoper.ui.geoList.PositionsAdapter;
import pl.tolichwer.gdziejestczoper.ui.mainView.GeoAdapter;

import dagger.Module;
import dagger.Provides;

@Module
class SearchFragmentModule {

    @Provides
    @SearchFragmentScope
    PositionsAdapter providePositionsAdapter() {
        return new PositionsAdapter();
    }

    @Provides
    @PositionListFragmentScope
    GeoAdapter provideGeoAdapter() {
        return new GeoAdapter();
    }
}
