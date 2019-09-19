package pl.tolichwer.gdziejestczoper.services;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import pl.tolichwer.gdziejestczoper.db.Repository;
import pl.tolichwer.gdziejestczoper.utils.Constants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import timber.log.Timber;

public class GeocodeAddressIntentService extends JobIntentService implements GeocodeAddressCallback {
    static final int JOB_ID = 2000;

    @Inject
    Repository repository;

    @Inject
    Geocoder geocoder;

    protected ResultReceiver resultReceiver;

    public GeocodeAddressIntentService() {
        super();
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, GeocodeAddressIntentService.class, JOB_ID, work);
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
        repository.setGeocodeAddressCallback(this);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Timber.d("Repository test = " + repository.getTestData());
        Timber.d("onHandleIntent from geocoder");
//        Geocoder geocoder = new Geocoder(this, new Locale("pl_PL"));
        List<Address> addresses;
        String addressString;

        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);


        double lat = location.getLatitude();
        double lng = location.getLongitude();


        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && addresses.size() > 0) {
                addressString = getAddressString(addresses);
                if (addressString==null){
                    addressString = getFirstPartOfAddress(addresses);
                }
                deliverResultToReceiver(Constants.SUCCESS_RESULT, addressString);
            }
            else {
                callGeocodingApi(lat, lng);
            }
        } catch (IOException e) {
            addressString = Constants.GEOCODING_FAILURE;
            deliverResultToReceiver(Constants.SUCCESS_RESULT, addressString);
        }
    }

    private String getFirstPartOfAddress(List<Address> addresses) {
        Address address = addresses.get(0);
        String[] splitAddress = address.getAddressLine(0).split(",");
        return splitAddress[0];
    }

    private void callGeocodingApi(double lat, double lng) {
        String address = String.format(Locale.ENGLISH, "https://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&location_type=ROOFTOP&result_type=point_of_interest&key=AIzaSyADPN7X3cxWbdMfpi5aHoikbaOv9N1L1LY", lat, lng);
        repository.getReverseGeocoding(address);
    }

    private String getAddressString(List<Address> addresses) {
        Address address = addresses.get(0);
        String addressString;
        if (address.getSubThoroughfare()!= null) {
            addressString = address.getThoroughfare() + ", " + address.getSubThoroughfare();
        } else {
            addressString = address.getThoroughfare();
        }
        return addressString;
    }


    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        resultReceiver.send(resultCode, bundle);
    }

    @Override
    public void onSuccessGetAddress(JsonObject address) {
        List<Address> retList = new ArrayList<>();

        JsonPrimitive jsonPrimitive = address.getAsJsonPrimitive("status");


        if ("OK".equalsIgnoreCase(jsonPrimitive.getAsString())) {
            JsonArray results = address.getAsJsonArray("results");
            if (results.size() > 0) {
                for (int i = 0; i < results.size() && i < 1; i++) {
                    JsonObject result = results.get(i).getAsJsonObject();
                    Address addr = new Address(Locale.getDefault());

                    JsonArray components = result.getAsJsonArray("address_components");
                    for (int a = 0; a < components.size(); a++) {
                        JsonObject component = components.get(a).getAsJsonObject();
                        JsonArray types = component.getAsJsonArray("types");
                        for (int j = 0; j < types.size(); j++) {
                            String type = types.get(j).getAsString();
                            switch (type) {
                                case "locality":
                                    addr.setLocality(component.get("long_name").getAsString());
                                    break;
                                case "street_number":
                                    addr.setSubThoroughfare(component.get("long_name").getAsString());
                                    break;
                                case "route":
                                    addr.setThoroughfare(component.get("long_name").getAsString());
                                    break;
                            }
                        }
                    }

                    addr.setLatitude(result.getAsJsonObject("geometry").getAsJsonObject("location").get("lat").getAsDouble());
                    addr.setLongitude(result.getAsJsonObject("geometry").getAsJsonObject("location").get("lng").getAsDouble());
                    retList.add(addr);
                }
            }
        }

        if (retList.size() > 0) {
            String addressString = retList.get(0).getThoroughfare() + ", " + retList.get(0).getSubThoroughfare();
            Timber.d("addressString = " + addressString);
            deliverResultToReceiver(Constants.SUCCESS_RESULT, addressString);
        }
    }

    @Override
    public void onFailureGetAddress() {
        deliverResultToReceiver(Constants.SUCCESS_RESULT, Constants.GEOCODING_FAILURE);
    }


    @Override
    public void onDestroy() {
        Timber.d("destroy from geocoding service");
        super.onDestroy();
    }
}
