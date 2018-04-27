//package com.example.asinit_user.gdziejestczoper.utils;
//
//
//import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
//import com.example.asinit_user.gdziejestczoper.viewobjects.Position;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonSerializationContext;
//import com.google.gson.JsonSerializer;
//
//import java.lang.reflect.Type;
//
//import timber.log.Timber;
//
//import static com.example.asinit_user.gdziejestczoper.utils.Converters.setSerializedDateString;
//
//public class PositionAdapter implements JsonSerializer<Position>{
//
//    @Override
//    public JsonElement serialize(Position src, Type typeOfSrc, JsonSerializationContext context) {
//        JsonObject obj = new JsonObject();
//        Timber.d("Serializing position: " + src.toString());
//
//        obj.addProperty("position_id",src.getPosition_id());
//        if (src.getEndDate() == null){
//            obj.addProperty("endDate","");
//        } else {
//            obj.addProperty("endDate", setSerializedDateString(src.getEndDate()));
//        }
//
//        if (src.getEndLocation() == null){
//            obj.addProperty("endLocation","");
//        } else {
//            obj.addProperty("endLocation",src.getEndLocation());
//        }
//
//        obj.addProperty("lastLocationDate",src.getLastLocationDate());
//        obj.addProperty("startDate",setSerializedDateString(src.getStartDate()));
//        obj.addProperty("startLocation",src.getStartLocation());
//        obj.addProperty("status", src.getStatus());
//
//        return obj;
//    }
//}
