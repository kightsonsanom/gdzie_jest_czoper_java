package pl.tolichwer.gdziejestczoper.viewobjects;

import android.location.Location;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import pl.tolichwer.gdziejestczoper.utils.Converters;

import java.util.UUID;

@Entity(tableName = Geo.TABLE_NAME)
public class Geo {

    public static final String TABLE_NAME = "geo";

    @PrimaryKey
    @NonNull
    private long id;
    private Location location;
    private long date;
    private String displayText;
    private int userID;

    public Geo() {
        id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    @Ignore
    public Geo(Location location, int userID) {
        id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.location = location;
        this.date = System.currentTimeMillis();
        this.userID = userID;
        displayText = "Date = " + Converters.longToString(date);
    }

    @Ignore
    public Geo(Location location, int userID, long date) {
        id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.location = location;
        this.date = date;
        this.userID = userID;
        displayText = "Date = " + Converters.longToString(date) + "\n" +
                "Locaiton(lat, lon) = " + location.getLatitude() + ", " + location.getLongitude();
    }


    @Ignore
    public Geo(@NonNull long geo_id, Location location, long date, String displayText, int userID) {
        this.id = geo_id;
        this.location = location;
        this.date = date;
        this.displayText = displayText;
        this.userID = userID;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "Geo{" +
                "geo_id=" + id +
                ", location=" + location +
                ", date=" + date +
                ", displayText='" + displayText + '\'' +
                ", userID=" + userID +
                '}';
    }
}


