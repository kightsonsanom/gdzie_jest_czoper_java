package com.example.asinit_user.gdziejestczoper.utils;


import android.databinding.BindingAdapter;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class customBindings {

    @BindingAdapter("initMap")
    public static void initMap(final MapView mapView, final LatLng latLng) {

        if (mapView != null) {
            mapView.onCreate(new Bundle());
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    // Add a marker
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Marker in India"));
                }
            });
        }
    }
}
