package com.example.asinit_user.mvvmapplication.ui.mainView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.asinit_user.mvvmapplication.R;
import com.example.asinit_user.mvvmapplication.services.GeoService;
import com.example.asinit_user.mvvmapplication.ui.geoList.PositionListFragment;
import com.example.asinit_user.mvvmapplication.ui.map.MapFragment;
import com.example.asinit_user.mvvmapplication.ui.search.SearchFragment;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import timber.log.Timber;

@RuntimePermissions
public class NavigationActivity extends AppCompatActivity implements HasSupportFragmentInjector {


    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    private FragmentManager fragmentManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    changeFragment(0);
                    return true;
                case R.id.navigation_dashboard:
                    changeFragment(1);
                    return true;
                case R.id.navigation_notifications:
                    changeFragment(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        NavigationActivityPermissionsDispatcher.startGeoServiceWithPermissionCheck(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            MapFragment mapFragment = new MapFragment();

            fragmentManager.beginTransaction()
                    .replace(R.id.navigation, mapFragment)
                    .commit();
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    private void changeFragment(int position) {
        Fragment selectedFragment = null;
        switch (position) {
            case 0:
                selectedFragment = new PositionListFragment();
                break;
            case 1:
                selectedFragment = new MapFragment();
                break;
            case 2:
                selectedFragment = new SearchFragment();
                break;
            case 3:
                selectedFragment = new MapFragment();

        }

        makeTransaction(position, selectedFragment);
    }

    private void makeTransaction(int position, Fragment selectedFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.navigation, selectedFragment);
        if (position == 3) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void startGeoService() {
        Intent intent = new Intent(getApplicationContext(), GeoService.class);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        NavigationActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
