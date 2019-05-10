package com.example.asinit_user.gdziejestczoper.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.asinit_user.gdziejestczoper.viewobjects.User;

import java.util.List;

@Dao
public interface LogDao {


    @Insert
    void insertLog(String log);

    @Query("SELECT * FROM log")
    List<User> getAllUsers();

}
