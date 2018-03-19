package com.example.asinit_user.gdziejestczoper.db;


import android.arch.persistence.room.TypeConverter;
import android.location.Location;
import android.location.LocationManager;

public class GeoTypeConverters {

    @TypeConverter
    public static String locationToString(Location location) {
        return location.getLongitude() + "," + location.getLatitude();
    }


    @TypeConverter
    public static Location stringToLocation(String locationString) {

        String[] latitudeAndLongitude = locationString.split(",");
        String longitude = latitudeAndLongitude[0];
        String latitude = latitudeAndLongitude[1];
        Location location = new Location(LocationManager.GPS_PROVIDER);

        location.setLatitude(Double.parseDouble(latitude));
        location.setLongitude(Double.parseDouble(longitude));

        return location;
    }
}
