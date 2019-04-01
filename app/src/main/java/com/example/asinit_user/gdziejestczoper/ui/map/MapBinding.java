package com.example.asinit_user.gdziejestczoper.ui.map;


import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

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

    @BindingAdapter({"initMap"})
    public static void initMap(final MapView mapView, final List<Geo> geoList) {

        if (mapView != null) {
            mapView.getMapAsync(googleMap -> {
                LatLng cameraPosition = new LatLng(51.9390826, 15.5154515);
                float cameraZoom = 13.01f;

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition, cameraZoom));

                googleMap.clear();

                // Add a marker
                if (geoList != null) {
                    Timber.d("geoList.size() = " + geoList.size());
                    for (Geo geo : geoList) {
                        Timber.d("geo = " + geo);
                        LatLng latLng = new LatLng(geo.getLocation().getLatitude(), geo.getLocation().getLongitude());
                        googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(getTitle(geo.getUser_id()))
                                .icon(getBitmapDescriptor(mapView.getContext(), geo.getUser_id())));
                    }
                }
            });
        }
    }

    @NonNull
    private static BitmapDescriptor getBitmapDescriptor(Context context, int user_id) {
        switch (user_id) {
            case 1:
                return bitmapDescriptorFromVector(context, R.mipmap.tomek_round);
//                return BitmapDescriptorFactory.fromResource(R.mipmap.tomek_round);
            case 2:
                return bitmapDescriptorFromVector(context, R.mipmap.pawel_round);
//                return BitmapDescriptorFactory.fromResource(R.mipmap.pawel_round);
            case 3:
                return bitmapDescriptorFromVector(context, R.mipmap.sala_round);
//                return BitmapDescriptorFactory.fromResource(R.mipmap.sala_round);
            case 4:
                return bitmapDescriptorFromVector(context, R.mipmap.krzysiek_round);
//                return BitmapDescriptorFactory.fromResource(R.mipmap.sala_round);
            default:
                return bitmapDescriptorFromVector(context, R.mipmap.tomek_round);
//                return BitmapDescriptorFactory.fromResource(R.mipmap.sala_round);
        }
    }

    private static String getTitle(int user_id) {
        switch (user_id) {
            case 1:
                return "Tomek";
            case 2:
                return "Maciej";
            case 3:
                return "Damian";
            case 4:
                return "Krzysiek";
            default:
                return "default";
        }
    }

    private static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        } else {
            vectorDrawable.setBounds(0, 0, (vectorDrawable.getIntrinsicWidth()/2), (vectorDrawable.getIntrinsicHeight()/2));
        }
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}