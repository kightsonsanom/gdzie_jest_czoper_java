package pl.tolichwer.gdziejestczoper.viewobjects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;


@Entity(tableName = Position.TABLE_NAME)
public class Position {

    public static final String TABLE_NAME = "position";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_END_DATE = "endDate";
    public static final String COLUMN_START_DATE = "startDate";
    public static final String COLUMN_LAST_LOCATION_DATE = "lastLocationDate";
    public static final String COLUMN_FIRST_LOCATION_DATE = "firstLocationDate";
    public static final String COLUMN_START_LOCATION = "startLocation";
    public static final String COLUMN_END_LOCATION = "endLocation";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_USER = "userID";

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = COLUMN_ID)
    private long id;
    @ColumnInfo(name = COLUMN_START_DATE)
    private String startDate;
    @ColumnInfo(name = COLUMN_END_DATE)
    private String endDate;
    @ColumnInfo(name = COLUMN_LAST_LOCATION_DATE)
    private long lastLocationDate;
    @ColumnInfo(name = COLUMN_FIRST_LOCATION_DATE)
    private long firstLocationDate;
    @ColumnInfo(name = COLUMN_START_LOCATION)
    private String startLocation;
    @ColumnInfo(name = COLUMN_END_LOCATION)
    private String endLocation;
    @ColumnInfo(name = COLUMN_STATUS)
    private int status;
    @ColumnInfo(name = COLUMN_USER)
    private int userID;

    public Position() {
        id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    @Ignore
    public Position(int userID) {
        this.id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.userID = userID;
    }


    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public long getLastLocationDate() {
        return lastLocationDate;
    }

    public void setLastLocationDate(long lastLocationDate) {
        this.lastLocationDate = lastLocationDate;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getFirstLocationDate() {
        return firstLocationDate;
    }

    public void setFirstLocationDate(long firstLocationDate) {
        this.firstLocationDate = firstLocationDate;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "Position{" +
                "position_id=" + id +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", lastLocationDate=" + lastLocationDate +
                ", firstLocationDate=" + firstLocationDate +
                ", startLocation='" + startLocation + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", status='" + status + '\'' +
                ", userID=" + userID +
                '}';
    }
}
