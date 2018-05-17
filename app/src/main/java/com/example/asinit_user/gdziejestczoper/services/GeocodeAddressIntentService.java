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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        Geocoder geocoder = new Geocoder(this, new Locale("pl_PL"));
        List<Address> addresses = null;

        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);


        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null) {
                Address address = addresses.get(0);
                String addressString;

                addressString = address.getThoroughfare() + ", " + address.getSubThoroughfare();
                deliverResultToReceiver(Constants.SUCCESS_RESULT, addressString);
            }
            else {
                Timber.d("geocoding addresses are null");
//                repeatGeocoding(Constants.FAILURE_RESULT, location);
                deliverResultToReceiver(Constants.SUCCESS_RESULT, "Geocoding poległ");
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

//    public static class MyGeocoder {
//
//        public static List<Address> getFromLocation(double lat, double lng, int maxResult) {
//
//            String address = String.format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=false&language=" + Locale.getDefault().getCountry(), lat, lng);
//            HttpGet httpGet = new HttpGet(address);
//            HttpClient client = new DefaultHttpClient();
//            client.getParams().setParameter(AllClientPNames.USER_AGENT, "Mozilla/5.0 (Java) Gecko/20081007 java-geocoder");
//            client.getParams().setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 5 * 1000);
//            client.getParams().setIntParameter(AllClientPNames.SO_TIMEOUT, 25 * 1000);
//            HttpResponse response;
//
//            List<Address> retList = null;
//
//            try {
//                response = client.execute(httpGet);
//                HttpEntity entity = response.getEntity();
//                String json = EntityUtils.toString(entity, "UTF-8");
//
//                JSONObject jsonObject = new JSONObject(json);
//
//                retList = new ArrayList<Address>();
//
//                if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
//                    JSONArray results = jsonObject.getJSONArray("results");
//                    if (results.length() > 0) {
//                        for (int i = 0; i < results.length() && i < maxResult; i++) {
//                            JSONObject result = results.getJSONObject(i);
//                            //Log.e(MyGeocoder.class.getName(), result.toString());
//                            Address addr = new Address(Locale.getDefault());
//                            // addr.setAddressLine(0, result.getString("formatted_address"));
//
//                            JSONArray components = result.getJSONArray("address_components");
//                            String streetNumber = "";
//                            String route = "";
//                            for (int a = 0; a < components.length(); a++) {
//                                JSONObject component = components.getJSONObject(a);
//                                JSONArray types = component.getJSONArray("types");
//                                for (int j = 0; j < types.length(); j++) {
//                                    String type = types.getString(j);
//                                    if (type.equals("locality")) {
//                                        addr.setLocality(component.getString("long_name"));
//                                    } else if (type.equals("street_number")) {
//                                        streetNumber = component.getString("long_name");
//                                        addr.setFeatureName(streetNumber);
//                                    } else if (type.equals("route")) {
//                                        route = component.getString("long_name");
//                                        addr.setThoroughfare(route);
//                                    }
//                                }
//                            }
//
//                            addr.setLatitude(result.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
//                            addr.setLongitude(result.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
//                            retList.add(addr);
//                        }
//                    }
//                }
//
//
//            } catch (IOException e) {
//                Timber.d("Error calling Google geocode webservice.", e);
//                return null;
//            } catch (JSONException e) {
//                Timber.d("Error parsing Google geocode webservice response.", e);
//                return null;
//            }
//
//            return retList;
//        }
//    }

    @Override
    public void onDestroy() {
        Timber.d("destroy from geocoding service");
        super.onDestroy();
    }
}
