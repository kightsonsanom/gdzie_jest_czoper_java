package com.example.asinit_user.mvvmapplication.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.asinit_user.mvvmapplication.db.entities.ActionEntity;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ActionDao {

    @Query("SELECT * FROM actions")
    LiveData<List<ActionEntity>> loadActions();

    @Query("SELECT * FROM actions i WHERE i.actionId = :actionId")
    LiveData<List<ActionEntity>> loadAction(int actionId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ActionEntity> actionEntityEntities);

    @Insert(onConflict = REPLACE)
    void insertAction(ActionEntity actionEntity);
}
