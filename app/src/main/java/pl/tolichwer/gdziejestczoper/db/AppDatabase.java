package pl.tolichwer.gdziejestczoper.db;



import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import pl.tolichwer.gdziejestczoper.db.dao.PositionDao;
import pl.tolichwer.gdziejestczoper.db.dao.GeoDao;
import pl.tolichwer.gdziejestczoper.db.dao.PositionGeoJoinDao;
import pl.tolichwer.gdziejestczoper.db.dao.UserDao;
import pl.tolichwer.gdziejestczoper.viewobjects.Position;
import pl.tolichwer.gdziejestczoper.viewobjects.Geo;
import pl.tolichwer.gdziejestczoper.viewobjects.PositionGeoJoin;
import pl.tolichwer.gdziejestczoper.viewobjects.User;

@Database(entities = {Position.class,
        Geo.class,
        PositionGeoJoin.class,
        User.class}, version = 1, exportSchema = false)
@TypeConverters({GeoTypeConverters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract PositionDao positionDao();
    public abstract GeoDao geoDao();
    public abstract PositionGeoJoinDao positionGeoJoinDao();
    public abstract UserDao userDao();

}
