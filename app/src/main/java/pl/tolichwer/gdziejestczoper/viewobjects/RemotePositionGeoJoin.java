package pl.tolichwer.gdziejestczoper.viewobjects;

/**
 *
 *  Class used for sending Geo to Position relationship to the backend.
 *
 */
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

    @Override
    public String toString() {
        return "RemotePositionGeoJoin{" +
                "positionId=" + positionId +
                ", geoId=" + geoId +
                '}';
    }
}
