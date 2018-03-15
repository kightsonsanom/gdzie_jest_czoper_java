package com.example.asinit_user.mvvmapplication.di;


import com.example.asinit_user.mvvmapplication.ui.mainView.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

// klasa odpowiedzialna za automatyczne generowanie subcomponentów, które będą wstrzykiwać obiekty do aktywnosci i fragmentow
// https://google.github.io/dagger/android

//Pro-tip: If your subcomponent and its builder have no other methods or supertypes than the ones mentioned in step #2,
//        you can use @ContributesAndroidInjector to generate them for you. Instead of steps 2 and 3,
//        add an abstract module method that returns your activity, annotate it with @ContributesAndroidInjector,
//        and specify the modules you want to install into the subcomponent. If the subcomponent needs scopes,
//        apply the scope annotations to the method as well.
@Module
public abstract class ActivityBuilderModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity contributeMainActivity();
}
