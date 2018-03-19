package com.example.asinit_user.gdziejestczoper.services;


import com.example.asinit_user.gdziejestczoper.db.entities.Geo;
import com.example.asinit_user.gdziejestczoper.db.entities.Position;

public interface PositionManagerCallback {

    void setLatestPositionFromDb(Position position);
    void setLatestGeoFromDb(Geo geo);
}
