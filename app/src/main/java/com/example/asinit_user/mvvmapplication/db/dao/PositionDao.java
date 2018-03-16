package com.example.asinit_user.mvvmapplication.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.asinit_user.mvvmapplication.db.entities.Position;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface PositionDao {

    @Query("SELECT * FROM position")
    LiveData<List<Position>> loadPositions();

    @Query("SELECT * FROM position i WHERE i.id = :positionID")
    LiveData<Position> loadPosition(String positionID);

    @Query("SELECT * FROM position ORDER BY lastLocationDate DESC LIMIT 1")
    Position loadLatestPosition();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Position> positions);

    @Insert(onConflict = REPLACE)
    void insertPosition (Position position);

    @Update(onConflict = REPLACE)
    void updatePosition(Position position);
}
