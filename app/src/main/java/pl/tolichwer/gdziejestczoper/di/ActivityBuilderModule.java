package pl.tolichwer.gdziejestczoper.di;

import pl.tolichwer.gdziejestczoper.ui.login.LoginActivity;
import pl.tolichwer.gdziejestczoper.ui.mainView.NavigationActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract NavigationActivity contributeNavigationActivity();
}
