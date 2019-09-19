package pl.tolichwer.gdziejestczoper.db.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import pl.tolichwer.gdziejestczoper.viewobjects.Geo;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface GeoDao {

    @Query("SELECT * FROM geo")
    LiveData<List<Geo>> loadGeos();

    @Query("SELECT * FROM geo i WHERE i.id = :geoId")
    LiveData<Geo> loadGeo(long geoId);

    @Query("SELECT * FROM geo g WHERE g.userID=:userID ORDER BY date DESC LIMIT 1")
    Geo loadLatestGeo(int userID);

    @Query("SELECT * FROM geo ORDER BY date DESC LIMIT 1")
    LiveData<Geo> loadLatestLiveDataGeo();

    @Query("SELECT * FROM geo ORDER BY date")
    List<Geo> getAllGeos();

    @Query("SELECT * FROM " + Geo.TABLE_NAME + " WHERE geo.date >= (SELECT geo.date FROM geo WHERE geo.id = :geoIdFromPreferences)")
    List<Geo> getGeosSinceFailure(long geoIdFromPreferences);

    @Insert(onConflict = REPLACE)
    void insertAll(List<Geo> geos);

    @Insert(onConflict = REPLACE)
    void insertGeo(Geo geo);

    @Query("SELECT * FROM geo g WHERE g.userID=:userID ORDER BY g.date DESC LIMIT 1 ")
    LiveData<Geo> loadLatestGeoForUser(int userID);

    @RawQuery(observedEntities = Geo.class)
    LiveData<List<Geo>> getLatestGeoForDistinctUsers(SupportSQLiteQuery query);
}