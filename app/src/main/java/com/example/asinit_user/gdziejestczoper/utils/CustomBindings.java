package com.example.asinit_user.gdziejestczoper.utils;


import android.databinding.BindingAdapter;
import android.os.Bundle;

import com.example.asinit_user.gdziejestczoper.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import timber.log.Timber;

public class CustomBindings {

    @BindingAdapter("initMap")
    public static void initMap(final MapView mapView, final LatLng latLng) {
        Timber.d("initMap method latLng = " + latLng);
        if (mapView != null) {
            mapView.getMapAsync(googleMap -> {

                LatLng cameraPosition = new LatLng(51.9390826,15.5154515);
                float cameraZoom = 13.01f;

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition, cameraZoom));

                // Add a marker
                if (latLng !=null) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("Ciastek")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.face_round)));
                }
            });
        }
    }

}
