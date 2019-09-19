package pl.tolichwer.gdziejestczoper.utils;


import pl.tolichwer.gdziejestczoper.viewobjects.Position;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class PositionAdapter implements JsonSerializer<Position> {

    @Override
    public JsonElement serialize(Position src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();

        obj.addProperty("id", src.getId());
        obj.addProperty("endDate", src.getEndDate());
        obj.addProperty("endLocation", src.getEndLocation());
        obj.addProperty("lastLocationDate", src.getLastLocationDate());
        obj.addProperty("firstLocationDate", src.getFirstLocationDate());
        obj.addProperty("startDate", src.getStartDate());
        obj.addProperty("startLocation", src.getStartLocation());
        obj.addProperty("status", src.getStatus());

        return obj;
    }
}
