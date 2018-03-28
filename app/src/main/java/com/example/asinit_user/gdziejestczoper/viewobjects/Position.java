package com.example.asinit_user.gdziejestczoper.viewobjects;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.ContentValues;
import android.support.annotation.NonNull;

import java.util.UUID;


@Entity(tableName = Position.TABLE_NAME)
public class Position {

    public static final String TABLE_NAME = "position";
    public static final String COLUMN_ID = "position_id";
    public static final String COLUMN_END_DATE = "endDate";
    public static final String COLUMN_START_DATE = "startDate";
    public static final String COLUMN_LAST_LOCATION_DATE = "lastLocationDate";
    public static final String COLUMN_START_LOCATION = "startLocation";
    public static final String COLUMN_END_LOCATION = "endLocation";
    public static final String COLUMN_STATUS = "status";


    @PrimaryKey
    @NonNull
    @ColumnInfo(name = COLUMN_ID)
    private long position_id;
    @ColumnInfo(name = COLUMN_START_DATE)
    private String startDate;
    @ColumnInfo(name = COLUMN_END_DATE)
    private String endDate;
    @ColumnInfo(name = COLUMN_LAST_LOCATION_DATE)
    private long lastLocationDate;
    @ColumnInfo(name = COLUMN_START_LOCATION)
    private String startLocation;
    @ColumnInfo(name = COLUMN_END_LOCATION)
    private String endLocation;
    @ColumnInfo(name = COLUMN_STATUS)
    private String status;


    public Position() {
        position_id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    @NonNull
    public long getPosition_id() {
        return position_id;
    }

    public void setPosition_id(@NonNull long position_id) {
        this.position_id = position_id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public long getLastLocationDate() {
        return lastLocationDate;
    }

    public void setLastLocationDate(long lastLocationDate) {
        this.lastLocationDate = lastLocationDate;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Position{" +
                "ID=" + position_id +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", lastLocationDate=" + lastLocationDate +
                ", startLocation='" + startLocation + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public static Position fromContentValues(ContentValues values) {
        final Position position = new Position();

        if (values.containsKey(COLUMN_ID)) {
            position.position_id = values.getAsLong(COLUMN_ID);
        }
        if (values.containsKey(COLUMN_END_DATE)) {
            position.startDate = values.getAsString(COLUMN_END_DATE);
        }
        if (values.containsKey(COLUMN_START_DATE)) {
            position.endDate = values.getAsString(COLUMN_START_DATE);
        }
        if (values.containsKey(COLUMN_LAST_LOCATION_DATE)) {
            position.lastLocationDate = values.getAsLong(COLUMN_LAST_LOCATION_DATE);
        }
        if (values.containsKey(COLUMN_START_LOCATION)) {
            position.startLocation = values.getAsString(COLUMN_START_LOCATION);
        }
        if (values.containsKey(COLUMN_END_LOCATION)) {
            position.endLocation = values.getAsString(COLUMN_END_LOCATION);
        }
        if (values.containsKey(COLUMN_STATUS)) {
            position.status = values.getAsString(COLUMN_STATUS);
        }
        return position;
    }


}
