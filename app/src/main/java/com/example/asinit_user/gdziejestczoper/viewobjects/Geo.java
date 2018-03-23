package com.example.asinit_user.gdziejestczoper.viewobjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.location.Location;
import android.support.annotation.NonNull;

import java.util.UUID;

@Entity(tableName = "geo")
public class Geo {

    @PrimaryKey
    @NonNull
    private String id;
    private Location location;
    private long date;
    private String displayText;

    public Geo() {
        id = String.valueOf(UUID.randomUUID());
    }

    public Geo(Location location, long date) {
        id = String.valueOf(UUID.randomUUID());
        this.location = location;
        this.date = date;
        displayText = "\nID = " + id + "\nLatitude = " + location.getLatitude() + "\nLongitude = " + location.getLongitude() + "\nDate = " + date;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
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

    @Override
    public String toString() {
        return "Geo{" +
                "ID=" + id +
                ", location=" + location +
                ", date=" + date +
                ", displayText='" + displayText + '\'' +
                '}';
    }
}


