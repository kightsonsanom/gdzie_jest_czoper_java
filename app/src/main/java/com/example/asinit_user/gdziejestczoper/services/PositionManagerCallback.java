package com.example.asinit_user.gdziejestczoper.services;


import android.location.Location;

import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;

public interface PositionManagerCallback {

    void setLatestPositionFromDb(Position position);
    void setLatestGeoFromDb(Geo geo);
    void setNewLocation(Location location);
}
