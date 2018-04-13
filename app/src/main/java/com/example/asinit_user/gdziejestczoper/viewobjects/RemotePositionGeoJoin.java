package com.example.asinit_user.gdziejestczoper.viewobjects;


public class RemotePositionGeoJoin {

    private long positionId;
    private long geoId;

    public RemotePositionGeoJoin() {
    }

    public RemotePositionGeoJoin(long positionId, long geoId) {
        this.positionId = positionId;
        this.geoId = geoId;
    }

    public long getPositionId() {
        return positionId;
    }

    public void setPositionId(long positionId) {
        this.positionId = positionId;
    }

    public long getGeoId() {
        return geoId;
    }

    public void setGeoId(long geoId) {
        this.geoId = geoId;
    }
}
