package pl.tolichwer.gdziejestczoper.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import pl.tolichwer.gdziejestczoper.viewobjects.PositionGeoJoin;
import pl.tolichwer.gdziejestczoper.viewobjects.Geo;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PositionGeoJoinDao {

    @Insert(onConflict = REPLACE)
    void insert (PositionGeoJoin positionGeoJoin);


//    @Query("SELECT * FROM geo " +
//            "INNER JOIN position_geo_join ON geo.id = position_geo_join.geoId " +
//            "WHERE position_geo_join.positionId=:positionId")
//    LiveData<List<Geo>> getGeoForPosition(final long positionId);

    @Query("SELECT * FROM (SELECT * FROM geo " +
            "INNER JOIN position_geo_join ON geo.id = position_geo_join.geoId " +
            "WHERE position_geo_join.positionId=:positionId)" +
            "ORDER BY date LIMIT 1")
    Geo getOldestGeoForPosition(final long positionId);

    @Query("SELECT * FROM position_geo_join WHERE position_geo_join.assignTime >= :positionIdFromPreferences")
    List<PositionGeoJoin> getAssignsSinceFailure(long positionIdFromPreferences);

    @Query("SELECT * FROM position_geo_join ORDER BY position_geo_join.assignTime DESC LIMIT 1")
    PositionGeoJoin getLastAssignment();
}
