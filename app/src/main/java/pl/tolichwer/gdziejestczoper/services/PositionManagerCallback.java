package pl.tolichwer.gdziejestczoper.services;


import android.location.Location;

import pl.tolichwer.gdziejestczoper.viewobjects.Geo;
import pl.tolichwer.gdziejestczoper.viewobjects.Position;

public interface PositionManagerCallback {

    void setLatestPositionFromDb(Position position);
    void setLatestGeoFromDb(Geo geo);
    void setNewLocation(Location location);
}
