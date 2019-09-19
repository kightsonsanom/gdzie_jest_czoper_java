package pl.tolichwer.gdziejestczoper.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import pl.tolichwer.gdziejestczoper.viewobjects.Position;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PositionDao {

    @Query("SELECT * FROM " + Position.TABLE_NAME)
    LiveData<List<Position>> loadPositions();

    @Query("SELECT * FROM " + Position.TABLE_NAME  + "  i WHERE i.id = :positionId")
    LiveData<Position> loadPosition(long positionId);

    @Query("SELECT * FROM " + Position.TABLE_NAME  + " WHERE position.userID=:userID ORDER BY lastLocationDate DESC LIMIT 1")
    Position loadLatestPosition(int userID);

//    @Query("SELECT * FROM " + Position.TABLE_NAME  + " WHERE position.startDate < :today")
//    List<Position> getPositionForToday(String today);

    @Insert(onConflict = REPLACE)
    void insertAll(List<Position> positions);

    @Insert(onConflict = REPLACE)
    void insertPosition (Position position);

    @Update(onConflict = REPLACE)
    void updatePosition(Position position);

    @Query("SELECT * FROM " + Position.TABLE_NAME + " ORDER BY lastLocationDate")
    List<Position> getAllPositions();

    @Query("SELECT * FROM " + Position.TABLE_NAME + " p INNER JOIN (SELECT userID from user WHERE name = :name) t on p.userID = t.userID WHERE (p.lastLocationDate > :searchFromDay AND p.lastLocationDate< :searchToDay)")
    LiveData<List<Position>> getLivePositionsFromRangeAndUser(String name, long searchFromDay, long searchToDay);

    @Query("SELECT * FROM " + Position.TABLE_NAME  + " WHERE (position.lastLocationDate > :searchFromDay AND position.lastLocationDate < :searchToDay)")
    List<Position> getPositionsFromRange(long searchFromDay, long searchToDay);

    @Query("SELECT * FROM " + Position.TABLE_NAME + " WHERE position.lastLocationDate >= (SELECT position.lastLocationDate FROM position WHERE position.id = :positionIdFromPreferences)")
    List<Position> getPositionsSinceFailure(long positionIdFromPreferences);

    @Query("SELECT * FROM " + Position.TABLE_NAME + " p INNER JOIN (SELECT userID from user WHERE name = :name) t on p.userID = t.userID WHERE (p.lastLocationDate > :fromDate AND p.firstLocationDate < :toDate) ")
    LiveData<List<Position>> loadPositionsForDayAndUser(String name, long fromDate, long toDate);
}
