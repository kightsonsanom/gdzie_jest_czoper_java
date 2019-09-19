package pl.tolichwer.gdziejestczoper.viewobjects;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;


/**
 *
 * Class used to provide many-to-many relationship between Geo and Position class within Room database.
 *
 */
@Entity(tableName = "position_geo_join",
        primaryKeys = {"positionId", "geoId"},
        foreignKeys = {
                @ForeignKey(entity = Position.class,
                        parentColumns = "id",
                        childColumns = "positionId"),
                @ForeignKey(entity = Geo.class,
                        parentColumns = "id",
                        childColumns = "geoId")
        })
public class PositionGeoJoin {

    @NonNull
    public final long positionId;
    @NonNull
    public final long geoId;
    private long assignTime;

    public PositionGeoJoin(@NonNull long positionId, @NonNull long geoId, long assignTime) {
        this.positionId = positionId;
        this.geoId = geoId;
        this.assignTime = assignTime;
    }

    @NonNull
    public long getPositionId() {
        return positionId;
    }

    @NonNull
    public long getGeoId() {
        return geoId;
    }

    public long getAssignTime() {
        return assignTime;
    }

    public void setAssignTime(long assignTime) {
        this.assignTime = assignTime;
    }

    @Override
    public String toString() {
        return "PositionGeoJoin{" +
                "positionId=" + positionId +
                ", geoId=" + geoId +
                ", assignTime=" + assignTime +
                '}';
    }
}
