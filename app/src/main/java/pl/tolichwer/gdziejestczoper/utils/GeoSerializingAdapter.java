package pl.tolichwer.gdziejestczoper.utils;


import pl.tolichwer.gdziejestczoper.viewobjects.Geo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class GeoSerializingAdapter implements JsonSerializer<Geo> {

    @Override
    public JsonElement serialize(Geo src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        String locationString = src.getLocation().getLatitude() + ", " + src.getLocation().getLongitude();
        obj.addProperty("id", src.getId());
        obj.addProperty("date", src.getDate());
        obj.addProperty("displayText", src.getDisplayText());
        obj.addProperty("location", locationString);

        return obj;
    }
}
