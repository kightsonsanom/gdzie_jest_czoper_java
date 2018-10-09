package com.example.asinit_user.gdziejestczoper.ui.map;


import android.databinding.BindingAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.asinit_user.gdziejestczoper.R;
import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.MapGeo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import timber.log.Timber;

public class MapBinding {

    @BindingAdapter("initMap")
    public static void initMap(final MapView mapView, final List<Geo> geoList) {
        if (mapView != null) {
            mapView.getMapAsync(googleMap -> {

                LatLng cameraPosition = new LatLng(51.9390826, 15.5154515);
                float cameraZoom = 13.01f;

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition, cameraZoom));

                // Add a marker
                if (geoList != null) {
                    Timber.d("geoList.size() = " + geoList.size());
                    for (Geo geo : geoList) {
                            LatLng latLng = new LatLng(geo.getLocation().getLatitude(), geo.getLocation().getLongitude());
                            googleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(String.valueOf(geo.getUser_id())));
//                                    .icon(getBitmapDescriptor(geo.getUser_id())));
                    }
                }
            });
        }
    }

    @NonNull
    private static BitmapDescriptor getBitmapDescriptor(int user_id) {
        switch (user_id) {
            case 1:
                return BitmapDescriptorFactory.fromResource(R.mipmap.sala_round);
            case 2:
                return BitmapDescriptorFactory.fromResource(R.mipmap.tomek_round);
            default:
                return BitmapDescriptorFactory.fromResource(R.mipmap.sala_round);
        }
    }
}