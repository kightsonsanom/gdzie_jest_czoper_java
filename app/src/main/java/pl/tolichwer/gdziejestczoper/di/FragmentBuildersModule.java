package pl.tolichwer.gdziejestczoper.di;


import pl.tolichwer.gdziejestczoper.di.scopes.PositionListFragmentScope;
import pl.tolichwer.gdziejestczoper.di.scopes.SearchFragmentScope;
import pl.tolichwer.gdziejestczoper.ui.geoList.PositionListFragment;
import pl.tolichwer.gdziejestczoper.ui.map.MapFragment;
import pl.tolichwer.gdziejestczoper.ui.search.SearchFragment;

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
