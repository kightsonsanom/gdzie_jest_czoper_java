package pl.tolichwer.gdziejestczoper.db;


import android.content.Context;
import android.content.SharedPreferences;

import pl.tolichwer.gdziejestczoper.R;

public class SharedPreferencesRepository {


    static final String DEFAULT_STRING = "defaultString";
    static final String USER_ID = "userID";


    private final SharedPreferences.Editor editor;
    private final SharedPreferences sharedPreferences;

    public SharedPreferencesRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    void putPositionID(long positionId) {
        editor.putLong("positionId", positionId);
        editor.commit();
    }


    long getPositionID() {
        return sharedPreferences.getLong("positionId", 0);
    }


    void putGeoID(long geoId) {
        editor.putLong("geoId", geoId);
        editor.commit();
    }


    long getGeoID() {
        return sharedPreferences.getLong("geoId", 0);
    }

    void setIsGeoSend(boolean status) {
        editor.putBoolean("isGeoSend", status);
        editor.commit();
    }

    void setIsPositionSend(boolean status) {
        editor.putBoolean("isPositionSend", status);
        editor.commit();
    }

    boolean getIsGeoSend() {
        return sharedPreferences.getBoolean("isGeoSend", false);
    }

    boolean getIsPositionSend() {
        return sharedPreferences.getBoolean("isPositionSend", false);
    }

    long getLastAssignedTime() {
        return sharedPreferences.getLong("lastAssignedTime", 0);
    }

    void setLastAssginedTime(long time) {
        editor.putLong("lastAssignedTime", time);
        editor.commit();

    }

    public void setUserID(int userID) {
        editor.putInt(USER_ID, userID);
        editor.commit();
    }

    public int getUserID() {
        return sharedPreferences.getInt(USER_ID, 0);
    }

    public void setErrorValue(String value) {
        editor.putString("errorValue", value);
        editor.commit();
    }


    String getErrorValue() {
        return sharedPreferences.getString("errorValue", DEFAULT_STRING);
    }

}
