package com.example.asinit_user.gdziejestczoper.db;


import android.arch.persistence.room.*;
import android.arch.persistence.room.TypeConverters;

import com.example.asinit_user.gdziejestczoper.db.dao.PositionDao;
import com.example.asinit_user.gdziejestczoper.db.dao.GeoDao;
import com.example.asinit_user.gdziejestczoper.db.dao.PositionGeoJoinDao;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;
import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.PositionGeoJoin;

@Database(entities = {Position.class, Geo.class, PositionGeoJoin.class}, version = 1, exportSchema = false)
@TypeConverters({GeoTypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract PositionDao positionDao();
    public abstract GeoDao geoDao();
    public abstract PositionGeoJoinDao positionGeoJoinDao();

}
