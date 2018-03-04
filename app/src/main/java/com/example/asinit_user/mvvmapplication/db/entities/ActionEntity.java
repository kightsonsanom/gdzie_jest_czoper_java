package com.example.asinit_user.mvvmapplication.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.example.asinit_user.mvvmapplication.model.Action;

@Entity(tableName = "actions")
public class ActionEntity implements Action {

    @PrimaryKey
    @ColumnInfo(name = "actionId")
    private int ID;
    private String tekst;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public ActionEntity(int ID, String tekst) {
        this.ID = ID;
        this.tekst = tekst;
    }

    @Override
    public String getText() {
        return null;
    }
}
