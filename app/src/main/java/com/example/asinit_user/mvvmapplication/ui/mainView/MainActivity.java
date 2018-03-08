package com.example.asinit_user.mvvmapplication.ui.mainView;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.asinit_user.mvvmapplication.R;
import com.example.asinit_user.mvvmapplication.ViewModelFactory;
import com.example.asinit_user.mvvmapplication.databinding.ActivityMainBinding;
import com.example.asinit_user.mvvmapplication.services.GeoService;
import com.example.asinit_user.mvvmapplication.ui.createView.CreateActionActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    ActionsAdapter actionsAdapter;

    @Inject
    GeoAdapter geoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        ActivityMainBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        MainActivityPermissionsDispatcher.startGeoServiceWithPermissionCheck(this);

        MainViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);

        mBinding.setModel(viewModel);

        viewModel.getObservableAction().observe(this, action -> {
            if (action!=null) {
                viewModel.setAction(action);
            }
        });

        mBinding.actionRecycler.setAdapter(actionsAdapter);
        mBinding.geoRecycler.setAdapter(geoAdapter);

        viewModel.getObservableActions().observe(this, actions -> {
            if (actions != null) {
                actionsAdapter.setActionList(actions);
            }
        });


        viewModel.getObservableGeos().observe(this, geos -> {
            if (geos != null) {
                geoAdapter.setGeoList(geos);
            }
        });


        mBinding.addActionBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateActionActivity.class));
            }
        );

    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void startGeoService() {
        Intent intent = new Intent(getApplicationContext(), GeoService.class);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }
}
