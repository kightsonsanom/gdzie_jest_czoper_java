package pl.tolichwer.gdziejestczoper.viewobjects;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

/**
 * Entity used to log location events during the runtime of the application.
 * It can be used to analyze and improve location processing algorithm contained in the GeoJobIntentService class.
 */
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
