package pl.tolichwer.gdziejestczoper.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pl.tolichwer.gdziejestczoper.ViewModelFactory;
import pl.tolichwer.gdziejestczoper.ui.geoList.PositionListFragmentViewModel;
import pl.tolichwer.gdziejestczoper.ui.map.MapViewModel;
import pl.tolichwer.gdziejestczoper.ui.search.SearchFragmentViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel.class)
    abstract ViewModel bindMapViewModel(MapViewModel mapViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(PositionListFragmentViewModel.class)
    abstract ViewModel bindPositionListFragmentViewModel(PositionListFragmentViewModel positionListFragmentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SearchFragmentViewModel.class)
    abstract ViewModel bindSearchFragmentViewModel(SearchFragmentViewModel searchFragmentViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}

