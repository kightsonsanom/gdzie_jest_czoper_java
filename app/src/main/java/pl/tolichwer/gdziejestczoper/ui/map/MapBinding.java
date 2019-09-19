package pl.tolichwer.gdziejestczoper.ui.map;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import pl.tolichwer.gdziejestczoper.R;
import pl.tolichwer.gdziejestczoper.viewobjects.Geo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapBinding {

    @BindingAdapter({"geoList", "userID"})
    public static void initMap(final MapView mapView, final List<Geo> geoList, final int userID) {

        if (mapView != null) {
            mapView.getMapAsync(googleMap -> {
                LatLng cameraPosition = null;
                float cameraZoom = 13.01f;
                googleMap.clear();

                if (geoList != null) {
                    for (Geo geo : geoList) {
                        LatLng latLng = new LatLng(geo.getLocation().getLatitude(), geo.getLocation().getLongitude());
                        googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(getTitle(geo.getUserID()))
                                .icon(getBitmapDescriptor(mapView.getContext(), geo.getUserID())));

                        if (geo.getUserID() == userID) {
                            cameraPosition = new LatLng(geo.getLocation().getLatitude(), geo.getLocation().getLongitude());
                        }
                    }
                }

                if (cameraPosition == null) {
                    cameraPosition = new LatLng(51.9390826, 15.5154515);
                }

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition, cameraZoom));

            });
        }
    }

    @NonNull
    private static BitmapDescriptor getBitmapDescriptor(Context context, int userID) {
        switch (userID) {
            case 1:
                return bitmapDescriptorFromVector(context, R.mipmap.tomek_round);
            case 2:
                return bitmapDescriptorFromVector(context, R.mipmap.pawel_round);
            case 3:
                return bitmapDescriptorFromVector(context, R.mipmap.sala_round);
            case 4:
                return bitmapDescriptorFromVector(context, R.mipmap.krzysiek_round);
            default:
                return bitmapDescriptorFromVector(context, R.mipmap.tomek_round);
        }
    }

    private static String getTitle(int userID) {
        switch (userID) {
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
            vectorDrawable.setBounds(0, 0, (vectorDrawable.getIntrinsicWidth() / 2), (vectorDrawable.getIntrinsicHeight() / 2));
        }
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}