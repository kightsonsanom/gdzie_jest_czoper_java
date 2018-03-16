package com.example.asinit_user.mvvmapplication.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.asinit_user.mvvmapplication.db.entities.PositionGeoJoin;
import com.example.asinit_user.mvvmapplication.db.entities.Geo;

import java.util.List;

@Dao
public interface PositionGeoJoinDao {

    @Insert
    void insert (PositionGeoJoin positionGeoJoin);


    @Query("SELECT * FROM geo " +
            "INNER JOIN position_geo_join ON geo.id = position_geo_join.geoID " +
            "WHERE position_geo_join.positionID=:positionID")
    LiveData<List<Geo>> getGeoForPosition(final String positionID);

    @Query("SELECT * FROM (SELECT * FROM geo " +
            "INNER JOIN position_geo_join ON geo.id = position_geo_join.geoID " +
            "WHERE position_geo_join.positionID=:positionID)" +
            "ORDER BY date LIMIT 1")
    Geo getOldestGeoForPosition(final String positionID);


}
