package com.example.asinit_user.mvvmapplication.di;

import android.app.Application;
import android.view.View;

import com.example.asinit_user.mvvmapplication.createView.CreateActionActivity;
import com.example.asinit_user.mvvmapplication.createView.CreateActionViewModel;
import com.example.asinit_user.mvvmapplication.mainView.MainActivity;
import com.example.asinit_user.mvvmapplication.mainView.MainViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, RoomModule.class, ViewModelModule.class})
public interface AppComponent {
    void inject(MainViewModel mainViewModel);
    void inject(CreateActionViewModel createActionViewModel);
    void inject(CreateActionActivity createActionActivity);
    void inject(MainActivity mainActivity);

    Application application();
}
