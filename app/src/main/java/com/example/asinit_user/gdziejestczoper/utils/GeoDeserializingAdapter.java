package com.example.asinit_user.gdziejestczoper.utils;


import android.location.Location;
import android.location.LocationManager;

import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import timber.log.Timber;

public class GeoDeserializingAdapter implements JsonDeserializer<Geo> {


    @Override
    public Geo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String [] locationString = jsonObject.get("location").getAsString().split(", ");

        Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(Double.parseDouble(locationString[0]));
            location.setLongitude(Double.parseDouble(locationString[1]));

        Geo geo = new Geo(
                jsonObject.get("geo_id").getAsLong(),
                location,
                jsonObject.get("date").getAsLong(),
                jsonObject.get("displayText").getAsString(),
                jsonObject.get("user_id").getAsInt()
        );

        Timber.d("geo from deserialization = " + geo);

        return geo;

    }
}
