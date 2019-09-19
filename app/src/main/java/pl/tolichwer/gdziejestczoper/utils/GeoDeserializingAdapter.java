package pl.tolichwer.gdziejestczoper.utils;


import android.location.Location;
import android.location.LocationManager;

import pl.tolichwer.gdziejestczoper.viewobjects.Geo;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class GeoDeserializingAdapter implements JsonDeserializer<Geo> {

    @Override
    public Geo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String[] locationString = jsonObject.get("location").getAsString().split(", ");


        if (locationString.length < 2) {
            locationString = new String[2];
            locationString[0] = "51.941067";
            locationString[1] = "15.504336";
        }

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(Double.parseDouble(locationString[0]));
        location.setLongitude(Double.parseDouble(locationString[1]));

        return new Geo(
                jsonObject.get("id").getAsLong(),
                location,
                jsonObject.get("date").getAsLong(),
                jsonObject.get("displayText").getAsString(),
                jsonObject.getAsJsonObject("user").get("userID").getAsInt()
        );

    }
}
