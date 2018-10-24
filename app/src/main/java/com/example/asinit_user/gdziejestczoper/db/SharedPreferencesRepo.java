package com.example.asinit_user.gdziejestczoper.db;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.asinit_user.gdziejestczoper.R;

public class SharedPreferencesRepo {


    public static final String DEFAULT_STRING = "defaultString";
    private final SharedPreferences.Editor editor;
    private final SharedPreferences sharedPreferences;

    public SharedPreferencesRepo (Context context){

        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        editor = sharedPreferences.edit();
    }


    public void putPositionID(long positionID){
        editor.putLong("positionID", positionID);
        editor.commit();
    }


    public long getPositionID(){
        return sharedPreferences.getLong("positionID",0);
    }


    public void putGeoID(long geoID){
        editor.putLong("geoID", geoID);
        editor.commit();
    }


    public long getGeoID(){
        return sharedPreferences.getLong("geoID",0);
    }

    public void setIsGeoSend(boolean status){
        editor.putBoolean("isGeoSend", status);
        editor.commit();
    }

    public void setIsPositionSend(boolean status){
        editor.putBoolean("isPositionSend", status);
        editor.commit();
    }

    public boolean getIsGeoSend(){
        return sharedPreferences.getBoolean("isGeoSend",false);
    }

    public boolean getIsPositionSend(){
        return sharedPreferences.getBoolean("isPositionSend",false);
    }

    public long getLastAssignedTime(){
        return sharedPreferences.getLong("lastAssignedTime", 0);
    }

    public void setLastAssginedTime(long time){
        editor.putLong("lastAssignedTime", time);
        editor.commit();

    }

    public void setUserID(int userID) {
        editor.putInt("userID", userID);
        editor.commit();
    }

    public int getUserID(){
        return sharedPreferences.getInt("userID",0);
    }

    public void setErrorValue(String value){
        editor.putString("errorValue", value);
        editor.commit();
    }


    public String getErrorValue(){
        return sharedPreferences.getString("errorValue", DEFAULT_STRING);
    }

}
