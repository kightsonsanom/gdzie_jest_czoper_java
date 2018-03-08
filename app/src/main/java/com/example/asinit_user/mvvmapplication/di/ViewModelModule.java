package com.example.asinit_user.mvvmapplication.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.asinit_user.mvvmapplication.ViewModelFactory;
import com.example.asinit_user.mvvmapplication.ui.createView.CreateActionViewModel;
import com.example.asinit_user.mvvmapplication.ui.mainView.MainActivity;
import com.example.asinit_user.mvvmapplication.ui.mainView.MainViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel bindAddEventViewModel(MainViewModel addEventViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CreateActionViewModel.class)
    abstract ViewModel bindEventListViewModel(CreateActionViewModel eventListViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}

