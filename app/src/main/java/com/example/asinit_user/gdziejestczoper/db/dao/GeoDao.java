package com.example.asinit_user.gdziejestczoper.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;

import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface GeoDao {

    @Query("SELECT * FROM geo")
    LiveData<List<Geo>> loadGeos();

    @Query("SELECT * FROM geo i WHERE i.id = :geoID")
    LiveData<Geo> loadGeo(long geoID);

    @Query("SELECT * FROM geo g WHERE g.user_id=:userID ORDER BY date DESC LIMIT 1")
    Geo loadLatestGeo(int userID);

    @Query("SELECT * FROM geo ORDER BY date DESC LIMIT 1")
    LiveData<Geo> loadLatestLiveDataGeo();

    @Query("SELECT * FROM geo ORDER BY date")
    List<Geo> getAllGeos();

    @Query("SELECT * FROM " + Geo.TABLE_NAME + " WHERE geo.date >= (SELECT geo.date FROM geo WHERE geo.id = :geoIDFromPreferences)")
    List<Geo> getGeosSinceFailure(long geoIDFromPreferences);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Geo> geos);

    @Insert(onConflict = REPLACE)
    void insertGeo(Geo geo);

    @Query("SELECT * FROM geo g WHERE g.user_id=:user_id ORDER BY g.date DESC LIMIT 1 ")
    LiveData<Geo> loadLatestGeoForUser(int user_id);

    @RawQuery(observedEntities = Geo.class)
    LiveData<List<Geo>> getLatestGeoForDistinctUsers(SupportSQLiteQuery query);
}