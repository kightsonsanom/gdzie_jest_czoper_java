package com.example.asinit_user.mvvmapplication.services;


import com.example.asinit_user.mvvmapplication.db.entities.Geo;
import com.example.asinit_user.mvvmapplication.db.entities.Position;

public interface PositionManagerCallback {

    void setLatestPositionFromDb(Position position);
    void setLatestGeoFromDb(Geo geo);
}
