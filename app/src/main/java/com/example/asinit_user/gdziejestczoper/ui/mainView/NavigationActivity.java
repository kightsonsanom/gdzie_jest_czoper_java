package com.example.asinit_user.gdziejestczoper.ui.mainView;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.asinit_user.gdziejestczoper.R;
import com.example.asinit_user.gdziejestczoper.services.AlarmReceiver;
import com.example.asinit_user.gdziejestczoper.ui.geoList.PositionListFragment;
import com.example.asinit_user.gdziejestczoper.ui.map.MapFragment;
import com.example.asinit_user.gdziejestczoper.ui.search.SearchFragment;

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

    //    public static final int START_SERVICE_INTERVAL = 10000;
    public static final int START_SERVICE_INTERVAL = 240000;
    //    public static final int START_SERVICE_INTERVAL = 240000000;
    public static final int FIRST_TRIGGER_INTERVAL = 5000;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    private FragmentManager fragmentManager;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
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
            bottomNavigationView.setSelectedItemId(R.id.map_view);
            changeFragment(1);
        }
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
                fragmentTag = getString(R.string.position_list_fragment);
                break;
            case 1:
                selectedFragment = new MapFragment();
                fragmentTag = getString(R.string.map_fragment);

                break;
            case 2:
                selectedFragment = new SearchFragment();
                fragmentTag = getString(R.string.search_fragment);

                break;
            case 3:
                selectedFragment = new MapFragment();
                fragmentTag = getString(R.string.map_fragment);
        }
        makeTransaction(position, selectedFragment, fragmentTag);
    }

    private void makeTransaction(int position, Fragment selectedFragment, String fragmentTag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, selectedFragment, fragmentTag);
        transaction.commit();
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WAKE_LOCK, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void startGeoService() {
        Context context = getApplicationContext();
        Intent intent = new Intent();
        String packageName = context.getPackageName();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm.isIgnoringBatteryOptimizations(packageName))
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        else {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
        }

        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            startActivity(intent);
        }


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
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
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        long triggerTime = calendar.getTimeInMillis();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = fragmentManager.findFragmentByTag(getString(R.string.position_list_fragment));

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
