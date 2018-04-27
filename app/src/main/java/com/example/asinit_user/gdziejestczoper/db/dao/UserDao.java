package com.example.asinit_user.gdziejestczoper.db.dao;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.asinit_user.gdziejestczoper.viewobjects.User;

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

    @Query("SELECT nazwa FROM user")
    LiveData<List<String>> getAllUserNames();
}
