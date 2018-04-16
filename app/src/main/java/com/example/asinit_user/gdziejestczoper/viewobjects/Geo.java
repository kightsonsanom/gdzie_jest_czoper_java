package com.example.asinit_user.gdziejestczoper.viewobjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;
import android.support.annotation.NonNull;

import java.util.UUID;

@Entity(tableName = Geo.TABLE_NAME)
public class Geo {

    public static final String TABLE_NAME = "geo";

    @PrimaryKey
    @NonNull
    private long geo_id;
    private Location location;
    private long date;
    private String displayText;
    private int user_id;

    public Geo() {
        geo_id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    public Geo(Location location, long date, int user_id) {
        geo_id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.location = location;
        this.date = date;
        this.user_id = user_id;
        displayText = "\nID = " + geo_id + "\nLatitude = " + location.getLatitude() + "\nLongitude = " + location.getLongitude() + "\nDate = " + date;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    @NonNull
    public long getGeo_id() {
        return geo_id;
    }

    public void setGeo_id(@NonNull long geo_id) {
        this.geo_id = geo_id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Geo{" +
                "geo_id=" + geo_id +
                ", location=" + location +
                ", date=" + date +
                ", displayText='" + displayText + '\'' +
                ", user_id=" + user_id +
                '}';
    }
}


