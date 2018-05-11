package com.example.asinit_user.gdziejestczoper.services;


import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.asinit_user.gdziejestczoper.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class GeocodeAddressIntentService extends IntentService {

    protected ResultReceiver resultReceiver;

    public GeocodeAddressIntentService() {
        super("GeocodingIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Timber.d("onHandleIntent from geocoder");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null) {
                Address address = addresses.get(0);
                String addressString;

                addressString = address.getThoroughfare() + ", " + address.getSubThoroughfare();
                deliverResultToReceiver(Constants.SUCCESS_RESULT, addressString);
            } else {
                Timber.d("geocoding addresses are null");
                repeatGeocoding(Constants.FAILURE_RESULT, location);
            }
        } catch (
                Exception e)

        {
            e.printStackTrace();

            Timber.d("geocoding failed");
            repeatGeocoding(Constants.FAILURE_RESULT, location);
            onDestroy();
        }

    }

    private void repeatGeocoding(int failureResult, Location location) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("location", location);
        resultReceiver.send(failureResult, bundle);
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        resultReceiver.send(resultCode, bundle);
    }


    @Override
    public void onDestroy() {
        Timber.d("destroy from geocoding service");
        super.onDestroy();
    }
}
