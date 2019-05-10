package com.example.asinit_user.gdziejestczoper.viewobjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.UUID;

@Entity(tableName = Log.TABLE_NAME)
public class Log {

    public static final String TABLE_NAME = "log";
    private String logMessage;

    @PrimaryKey
    @NonNull
    private long id;

    public Log() {
        id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }


    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Log{" +
                "logMessage='" + logMessage + '\'' +
                ", id=" + id +
                '}';
    }
}
