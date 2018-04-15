package com.example.asinit_user.gdziejestczoper.viewobjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = User.TABLE_NAME)
public class User {

    public static final String TABLE_NAME = "user";

    @PrimaryKey
    @NonNull
    private int user_id;

    private String nazwa;
    private String login;
    private String password;


    @NonNull
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(@NonNull int user_id) {
        this.user_id = user_id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
