package com.example.asinit_user.gdziejestczoper.ui.mainView;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.asinit_user.gdziejestczoper.R;
import com.example.asinit_user.gdziejestczoper.services.AlarmReceiver;
import com.example.asinit_user.gdziejestczoper.ui.geoList.PositionListFragment;
import com.example.asinit_user.gdziejestczoper.ui.map.MapFragment;
import com.example.asinit_user.gdziejestczoper.ui.search.SearchFragment;

import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import timber.log.Timber;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

@RuntimePermissions
public class NavigationActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    public static final int INTENT_ID = 100;
    public static final int LOG_INTENT_ID = 200;
    public static final String POSITION_LIST_FRAGMENT = "PositionListFragment";
    public static final String MAP_FRAGMENT = "MapFragment";
    public static final String SEARCH_FRAGMENT = "SearchFragment";

//    public static final int START_SERVICE_INTERVAL = 10000;
    public static final int START_SERVICE_INTERVAL = 60000;
//    public static final int START_SERVICE_INTERVAL = 240000000;
    public static final int FIRST_TRIGGER_INTERVAL = 5000;

    public static final int MIN_5 = 300000;
    public static final int MIN_60 =3600000;


    public ArrayList<Location> locations;


    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;


    private FragmentManager fragmentManager;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.geo_list_view:
                    NavigationActivity.this.changeFragment(0);
                    break;
                case R.id.map_view:
                    NavigationActivity.this.changeFragment(1);
                    break;
                case R.id.search_view:
                    NavigationActivity.this.changeFragment(2);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        NavigationActivityPermissionsDispatcher.startGeoServiceWithPermissionCheck(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            locations = new ArrayList<>();

//RUCH - Wiezienie -> PCK
            locations.add(dodajLokacje(15.494357, 51.936770,  System.currentTimeMillis() + (MIN_5 )));
            locations.add(dodajLokacje(15.493624,51.939867,  System.currentTimeMillis() + (MIN_5 *2)));

//POSTOJ - Sucharskiego
            locations.add(dodajLokacje(15.490865,51.934473, System.currentTimeMillis() + (MIN_5 *3)));
            locations.add(dodajLokacje(15.490865,51.934473, System.currentTimeMillis() + (MIN_5 *4)));
            locations.add(dodajLokacje(15.490865,51.934473, System.currentTimeMillis() + (MIN_5 *5)));

//PRZERWA
            locations.add(dodajLokacje(15.493624,51.939867,  System.currentTimeMillis() + (MIN_60) + (MIN_5 *6)));
            locations.add(dodajLokacje(15.493624,51.939867,  System.currentTimeMillis() + (MIN_60*2) + (MIN_5 * 7)));

//POSTOJ - PCK
            locations.add(dodajLokacje( 15.493624,51.939867,  System.currentTimeMillis() + (1552082160000l - System.currentTimeMillis())));

            bottomNavigationView.setSelectedItemId(R.id.map_view);
            changeFragment(1);
        }

    }

    public Location getLocation(){
        return locations.remove(0);
    }


    private Location dodajLokacje(double longitude, double latitude, long time) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTime(time);
        return location;
    }
    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    private void changeFragment(int position) {
        String fragmentTag = null;
        Fragment selectedFragment = null;

        switch (position) {
            case 0:
                selectedFragment = new PositionListFragment();
                fragmentTag = POSITION_LIST_FRAGMENT;
                break;
            case 1:
                selectedFragment = new MapFragment();
                fragmentTag = MAP_FRAGMENT;

                break;
            case 2:
                selectedFragment = new SearchFragment();
                fragmentTag = SEARCH_FRAGMENT;

                break;
            case 3:
                selectedFragment = new MapFragment();
                fragmentTag = MAP_FRAGMENT;
        }
        makeTransaction(position, selectedFragment, fragmentTag);
    }

    private void makeTransaction(int position, Fragment selectedFragment, String fragmentTag) {
        Timber.d("Entering fragment number :" + position + " fragment name :" + selectedFragment.toString());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, selectedFragment, fragmentTag);
        transaction.commit();
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WAKE_LOCK, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void startGeoService() {

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        startGeoAlarm(alarmManager);
        startLogAlarm(alarmManager);
    }



    private void startGeoAlarm(AlarmManager alarmManager) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("GEO_ALARM");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, FIRST_TRIGGER_INTERVAL, START_SERVICE_INTERVAL, pendingIntent);

    }

    private void startLogAlarm(AlarmManager alarmManager) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("LOG_ALARM");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND,0);
        long triggerTime = calendar.getTimeInMillis();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,triggerTime, AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = fragmentManager.findFragmentByTag(POSITION_LIST_FRAGMENT);

        if (currentFragment != null && currentFragment.isVisible()) {
            PositionListFragment positionListFragment = (PositionListFragment) currentFragment;

            if (!positionListFragment.isUserListVisible()) {
                positionListFragment.returnToUserList();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        NavigationActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
