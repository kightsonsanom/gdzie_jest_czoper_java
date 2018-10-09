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
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_END_DATE = "endDate";
    public static final String COLUMN_START_DATE = "startDate";
    public static final String COLUMN_LAST_LOCATION_DATE = "lastLocationDate";
    public static final String COLUMN_FIRST_LOCATION_DATE = "firstLocationDate";
    public static final String COLUMN_START_LOCATION = "startLocation";
    public static final String COLUMN_END_LOCATION = "endLocation";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_USER = "user_id";

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = COLUMN_ID)
    private long id;
    @ColumnInfo(name = COLUMN_START_DATE)
    private String startDate;
    @ColumnInfo(name = COLUMN_END_DATE)
    private String endDate;
    @ColumnInfo(name = COLUMN_LAST_LOCATION_DATE)
    private long lastLocationDate;
    @ColumnInfo(name = COLUMN_FIRST_LOCATION_DATE)
    private long firstLocationDate;
    @ColumnInfo(name = COLUMN_START_LOCATION)
    private String startLocation;
    @ColumnInfo(name = COLUMN_END_LOCATION)
    private String endLocation;
    @ColumnInfo(name = COLUMN_STATUS)
    private String status;
    @ColumnInfo(name = COLUMN_USER)
    private int user_id;

    public Position() {
        id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    public Position(int user_id) {
        this.id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.user_id = user_id;
    }


    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
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

    public long getFirstLocationDate() {
        return firstLocationDate;
    }

    public void setFirstLocationDate(long firstLocationDate) {
        this.firstLocationDate = firstLocationDate;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Position{" +
                "position_id=" + id +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", lastLocationDate=" + lastLocationDate +
                ", firstLocationDate=" + firstLocationDate +
                ", startLocation='" + startLocation + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", status='" + status + '\'' +
                ", user_id=" + user_id +
                '}';
    }
}
