package com.example.asinit_user.mvvmapplication.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.asinit_user.mvvmapplication.db.entities.Geo;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface GeoDao {

    @Query("SELECT * FROM geo")
    LiveData<List<Geo>> loadGeos();

    @Query("SELECT * FROM geo i WHERE i.geoID = :geoID")
    LiveData<Geo> loadGeo(int geoID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Geo> geos);

    @Insert(onConflict = REPLACE)
    void insertGeo(Geo geo);
}