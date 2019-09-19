package pl.tolichwer.gdziejestczoper.db.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import pl.tolichwer.gdziejestczoper.viewobjects.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user WHERE EXISTS (SELECT * FROM user WHERE (user.login = :login AND user.password = :password))")
    LiveData<List<User>> getUsers(String login, String password);

    @Query("SELECT * FROM user")
    List<User> getAllUsers();

    @Query("SELECT * FROM user")
    LiveData<List<User>> getAllLiveUsers();

    @Insert
    void insertAll(List<User> userList);

    @Query("SELECT name FROM user")
    LiveData<List<String>> getAllUserNames();

    @Query("SELECT userID FROM user WHERE name = :name")
    int getUserID(String name);
}
