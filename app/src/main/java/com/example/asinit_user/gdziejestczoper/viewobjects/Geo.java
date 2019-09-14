package com.example.asinit_user.gdziejestczoper.viewobjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;
import android.support.annotation.NonNull;

import com.example.asinit_user.gdziejestczoper.utils.Converters;

import java.util.UUID;

@Entity(tableName = Geo.TABLE_NAME)
public class Geo {

    public static final String TABLE_NAME = "geo";

    @PrimaryKey
    @NonNull
    private long id;
    private Location location;
    private long date;
    private String displayText;
    private int user_id;

    public Geo() {
        id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    public Geo(Location location, int user_id) {
        id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.location = location;
        this.date = System.currentTimeMillis();
        this.user_id = user_id;
        displayText = "Date = " + Converters.longToString(date);
    }

    public Geo(Location location, int user_id, long date) {
        id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.location = location;
        this.date = date;
        this.user_id = user_id;
        displayText = "Date = " + Converters.longToString(date) + "\n" +
                "Locaiton(lat, lon) = " + location.getLatitude() + ", " + location.getLongitude();
    }


    public Geo(@NonNull long geo_id, Location location, long date, String displayText, int user_id) {
        this.id = geo_id;
        this.location = location;
        this.date = date;
        this.displayText = displayText;
        this.user_id = user_id;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
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
                "geo_id=" + id +
                ", location=" + location +
                ", date=" + date +
                ", displayText='" + displayText + '\'' +
                ", user_id=" + user_id +
                '}';
    }
}


