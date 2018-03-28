package com.example.asinit_user.gdziejestczoper.utils;


import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import timber.log.Timber;

public class GeoAdapter implements JsonSerializer<Geo> {


    @Override
    public JsonElement serialize(Geo src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        Timber.d("Serializing geo: " + src.toString());
        String locationString = src.getLocation().getLatitude()+", " + src.getLocation().getLongitude();
        obj.addProperty("geo_id",src.getGeo_id());
        obj.addProperty("date",src.getDate());
        obj.addProperty("displayText",src.getDisplayText());
        obj.addProperty("location", locationString);

        return obj;
    }
}
