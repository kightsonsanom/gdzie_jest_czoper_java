package com.example.asinit_user.mvvmapplication.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.example.asinit_user.mvvmapplication.db.dao.ActionDao;
import com.example.asinit_user.mvvmapplication.db.entities.ActionEntity;

@Database(entities = ActionEntity.class, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ActionDao actionDao();

}
