package com.example.asinit_user.gdziejestczoper.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import com.example.asinit_user.gdziejestczoper.viewobjects.Position;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface PositionDao {

    @Query("SELECT * FROM " + Position.TABLE_NAME)
    LiveData<List<Position>> loadPositions();

    @Query("SELECT * FROM " + Position.TABLE_NAME  + "  i WHERE i.position_id = :positionID")
    LiveData<Position> loadPosition(long positionID);

    @Query("SELECT * FROM " + Position.TABLE_NAME  + " ORDER BY lastLocationDate DESC LIMIT 1")
    Position loadLatestPosition();

    @Query("SELECT * FROM " + Position.TABLE_NAME  + " WHERE position.startDate < :today")
    List<Position> getPositionForToday(String today);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Position> positions);

    @Insert(onConflict = REPLACE)
    void insertPosition (Position position);

    @Update(onConflict = REPLACE)
    void updatePosition(Position position);

    @Query("SELECT * FROM " + Position.TABLE_NAME)
    List<Position> getAllPositions();

    @Query("SELECT * FROM " + Position.TABLE_NAME  + " WHERE (position.startDate > :searchFromDay AND position.startDate < :searchToDay)")
    LiveData<List<Position>> getLivePositionsFromRange(String searchFromDay, String searchToDay);

    @Query("SELECT * FROM " + Position.TABLE_NAME  + " WHERE (position.startDate > :searchFromDay AND position.startDate < :searchToDay)")
    List<Position> getPositionsFromRange(String searchFromDay, String searchToDay);


    /** Methods for communication with content provider*/

    @Insert
    long insert(Position position);

    @Insert
    long[] insertAll(Position[] positions);

    @Query("SELECT * FROM " + Position.TABLE_NAME)
    Cursor selectAll();

    @Query("SELECT * FROM " + Position.TABLE_NAME  + " WHERE position.position_id = :id")
    Cursor selectById(long id);

    @Query("DELETE FROM " + Position.TABLE_NAME  + " WHERE position.position_id = :id")
    int deleteById(long id);

    @Update
    int update(Position position);
}
