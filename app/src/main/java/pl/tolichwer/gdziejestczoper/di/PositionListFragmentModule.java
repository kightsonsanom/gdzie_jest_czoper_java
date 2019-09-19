package pl.tolichwer.gdziejestczoper.di;


import pl.tolichwer.gdziejestczoper.di.scopes.PositionListFragmentScope;
import pl.tolichwer.gdziejestczoper.ui.mainView.GeoAdapter;
import pl.tolichwer.gdziejestczoper.ui.geoList.PositionsAdapter;

import dagger.Module;
import dagger.Provides;

@Module
class PositionListFragmentModule {

    @Provides
    @PositionListFragmentScope
    PositionsAdapter providePositionsAdapter() {
        return new PositionsAdapter();
    }

    @Provides
    @PositionListFragmentScope
    GeoAdapter provideGeoAdapter() {
        return new GeoAdapter();
    }
}
