package com.example.asinit_user.gdziejestczoper.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.MapGeo;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface GeoDao {

    @Query("SELECT * FROM geo")
    LiveData<List<Geo>> loadGeos();

    @Query("SELECT * FROM geo i WHERE i.geo_id = :geoID")
    LiveData<Geo> loadGeo(long geoID);

    @Query("SELECT * FROM geo ORDER BY date DESC LIMIT 1")
    Geo loadLatestGeo();

    @Query("SELECT * FROM geo ORDER BY date DESC LIMIT 1")
    LiveData<Geo> loadLatestLiveDataGeo();

    @Query("SELECT * FROM geo")
    List<Geo> getAllGeos();

    @Query("SELECT * FROM " + Geo.TABLE_NAME + " WHERE geo.date >= (SELECT geo.date FROM geo WHERE geo.geo_id = :geoIDFromPreferences)")
    List<Geo> getGeosSinceFailure(long geoIDFromPreferences);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Geo> geos);

    @Insert(onConflict = REPLACE)
    void insertGeo(Geo geo);

    @Query("SELECT * FROM geo g WHERE g.user_id=:user_id ORDER BY g.date DESC LIMIT 1 ")
    LiveData<Geo> loadLatestGeoForUser(int user_id);
}