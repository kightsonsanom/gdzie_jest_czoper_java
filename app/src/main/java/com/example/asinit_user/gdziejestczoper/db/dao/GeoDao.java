package com.example.asinit_user.gdziejestczoper.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface GeoDao {

    @Query("SELECT * FROM geo")
    LiveData<List<Geo>> loadGeos();

    @Query("SELECT * FROM geo i WHERE i.id = :geoID")
    LiveData<Geo> loadGeo(String geoID);

    @Query("SELECT * FROM geo ORDER BY date DESC LIMIT 1")
    Geo loadLatestGeo();

    @Query("SELECT * FROM geo ORDER BY date DESC LIMIT 1")
    LiveData<Geo> loadLatestLiveDataGeo();

    @Query("SELECT * FROM geo")
    List<Geo> getAllGeos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Geo> geos);

    @Insert(onConflict = REPLACE)
    void insertGeo(Geo geo);


}