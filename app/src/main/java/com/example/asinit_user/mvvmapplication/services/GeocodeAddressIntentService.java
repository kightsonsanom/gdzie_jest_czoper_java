package com.example.asinit_user.mvvmapplication.services;


import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.asinit_user.mvvmapplication.R;
import com.example.asinit_user.mvvmapplication.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeocodeAddressIntentService extends IntentService {

    protected ResultReceiver resultReceiver;

    public GeocodeAddressIntentService() {
        super("GeocodingIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = addresses.get(0);
        ArrayList<String> addressFragments = new ArrayList<String>();

        for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressFragments.add(address.getAddressLine(i));
        }
        deliverResultToReceiver(Constants.SUCCESS_RESULT, TextUtils.join(System.getProperty("line.separator"),
                        addressFragments));

    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        resultReceiver.send(resultCode, bundle);
    }

}
