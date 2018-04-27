package com.example.asinit_user.gdziejestczoper.viewobjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.support.annotation.NonNull;

@Entity(tableName = "position_geo_join",
        primaryKeys = {"positionId", "geoId"},
        foreignKeys = {
                @ForeignKey(entity = Position.class,
                        parentColumns = "position_id",
                        childColumns = "positionId"),
                @ForeignKey(entity = Geo.class,
                        parentColumns = "geo_id",
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
