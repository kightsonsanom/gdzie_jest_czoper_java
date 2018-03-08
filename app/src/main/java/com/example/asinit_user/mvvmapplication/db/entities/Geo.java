package com.example.asinit_user.mvvmapplication.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "geo")
public class Geo {

    @PrimaryKey
    @ColumnInfo(name = "geoID")
    @NonNull
    private String geoID;
    private String location;


    public Geo(String geoID, String location) {
        this.geoID = geoID;
        this.location = location;
    }

    public String getGeoID() {
        return geoID;
    }

    public void setGeoID(String geoID) {
        this.geoID = geoID;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Geo{" +
                "geoID='" + geoID + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}


