package com.example.asinit_user.mvvmapplication.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.asinit_user.mvvmapplication.ViewModelFactory;
import com.example.asinit_user.mvvmapplication.ui.geoList.PositionListFragment;
import com.example.asinit_user.mvvmapplication.ui.geoList.PositionListFragmentViewModel;
import com.example.asinit_user.mvvmapplication.ui.mainView.MainViewModel;
import com.example.asinit_user.mvvmapplication.ui.map.MapViewModel;
import com.example.asinit_user.mvvmapplication.ui.search.SearchFragmentViewModel;

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

