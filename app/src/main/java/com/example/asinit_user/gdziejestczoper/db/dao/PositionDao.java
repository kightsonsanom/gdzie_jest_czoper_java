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

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface PositionDao {

    @Query("SELECT * FROM " + Position.TABLE_NAME)
    LiveData<List<Position>> loadPositions();

    @Query("SELECT * FROM " + Position.TABLE_NAME  + "  i WHERE i.position_id = :positionID")
    LiveData<Position> loadPosition(long positionID);

    @Query("SELECT * FROM " + Position.TABLE_NAME  + " ORDER BY lastLocationDate DESC LIMIT 1")
    Position loadLatestPosition();

//    @Query("SELECT * FROM " + Position.TABLE_NAME  + " WHERE position.startDate < :today")
//    List<Position> getPositionForToday(String today);

    @Insert(onConflict = IGNORE)
    void insertAll(List<Position> positions);

    @Insert(onConflict = REPLACE)
    void insertPosition (Position position);

    @Update(onConflict = REPLACE)
    void updatePosition(Position position);

    @Query("SELECT * FROM " + Position.TABLE_NAME)
    List<Position> getAllPositions();

    @Query("SELECT * FROM " + Position.TABLE_NAME  + " WHERE (position.lastLocationDate > :searchFromDay AND position.lastLocationDate< :searchToDay)")
    LiveData<List<Position>> getLivePositionsFromRange(long searchFromDay, long searchToDay);

    @Query("SELECT * FROM " + Position.TABLE_NAME  + " WHERE (position.lastLocationDate > :searchFromDay AND position.lastLocationDate < :searchToDay)")
    List<Position> getPositionsFromRange(long searchFromDay, long searchToDay);

    @Query("SELECT * FROM " + Position.TABLE_NAME + " WHERE position.lastLocationDate >= (SELECT position.lastLocationDate FROM position WHERE position.position_id = :positionIDFromPreferences)")
    List<Position> getPositionsSinceFailure(long positionIDFromPreferences);

    @Query("SELECT * FROM " + Position.TABLE_NAME + " p INNER JOIN (SELECT user_id from user WHERE nazwa = :nazwa) t on p.user_id = t.user_id WHERE (p.firstLocationDate > :fromDate AND p.lastLocationDate < :toDate) ")
    LiveData<List<Position>> loadPositionsForDayAndUser(String nazwa, long fromDate, long toDate);
}
