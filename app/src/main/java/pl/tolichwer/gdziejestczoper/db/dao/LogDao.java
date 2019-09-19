package pl.tolichwer.gdziejestczoper.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import pl.tolichwer.gdziejestczoper.viewobjects.User;

import java.util.List;

@Dao
public interface LogDao {


    @Insert
    void insertLog(String log);

    @Query("SELECT * FROM log")
    List<User> getAllUsers();

}
