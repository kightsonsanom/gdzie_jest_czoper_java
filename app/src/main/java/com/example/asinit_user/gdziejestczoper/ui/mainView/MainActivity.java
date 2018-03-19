//package com.example.asinit_user.mvvmapplication.ui.mainView;
//
//import android.Manifest;
//import android.arch.lifecycle.ViewModelProviders;
//import android.content.Intent;
//import android.databinding.DataBindingUtil;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.DisplayMetrics;
//
//import com.example.asinit_user.mvvmapplication.R;
//import com.example.asinit_user.mvvmapplication.ViewModelFactory;
//import com.example.asinit_user.mvvmapplication.databinding.ActivityMainBinding;
//import com.example.asinit_user.mvvmapplication.services.GeoService;
//
//import javax.inject.Inject;
//
//import dagger.android.AndroidInjection;
//import permissions.dispatcher.NeedsPermission;
//import permissions.dispatcher.RuntimePermissions;
//
//@RuntimePermissions
//public class MainActivity extends AppCompatActivity {
//
//    @Inject
//    ViewModelFactory viewModelFactory;
//
//    @Inject
//    PositionsAdapter positionsAdapter;
//
//    @Inject
//    GeoAdapter geoAdapter;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        AndroidInjection.inject(this);
//        super.onCreate(savedInstanceState);
//        ActivityMainBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//
//        setRecyclersHeight(mBinding);
//
//        MainActivityPermissionsDispatcher.startGeoServiceWithPermissionCheck(this);
//
//        MainViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);
//
//        mBinding.setModel(viewModel);
//
////        mBinding.getLatestGeoBtn.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                mBinding.latestGeoFromButtonTextView.setText(viewModel.getLatestGeo());
////            }
////        });
////
////
////        viewModel.getObservableGeo().observe(this, new Observer<Geo>() {
////            @Override
////            public void onChanged(@Nullable Geo geo) {
////                viewModel.setGeo(geo);
////            }
////        });
//
//
//        mBinding.positionRecycler.setAdapter(positionsAdapter);
//        mBinding.geoRecycler.setAdapter(geoAdapter);
//
//        viewModel.getObservablePositions().observe(this, positions-> {
//            if (positions != null) {
//                positionsAdapter.setPositionsList(positions);
//            }
//        });
//
//
//        viewModel.getObservableGeos().observe(this, geos -> {
//            if (geos != null) {
//                geoAdapter.setGeoList(geos);
//            }
//        });
//
//    }
//
//    private void setRecyclersHeight(ActivityMainBinding mBinding) {
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//
//        int height =  (displaymetrics.heightPixels*35)/100;
//
//        mBinding.positionRecycler.getLayoutParams().height = height;
//        mBinding.geoRecycler.getLayoutParams().height = height;
//    }
//
//    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//    public void startGeoService() {
//        Intent intent = new Intent(getApplicationContext(), GeoService.class);
//        startService(intent);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
//    }
//}
