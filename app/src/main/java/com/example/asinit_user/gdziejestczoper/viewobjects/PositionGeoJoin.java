package com.example.asinit_user.gdziejestczoper.viewobjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

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
    public final String positionId;
    @NonNull
    public final String geoId;

    public PositionGeoJoin(@NonNull String positionId, @NonNull String geoId) {
        this.positionId = positionId;
        this.geoId = geoId;
    }

    @Override
    public String toString() {
        return "PositionGeoJoin{" +
                "positionID=" + positionId +
                ", geoID=" + geoId +
                '}';
    }
}
