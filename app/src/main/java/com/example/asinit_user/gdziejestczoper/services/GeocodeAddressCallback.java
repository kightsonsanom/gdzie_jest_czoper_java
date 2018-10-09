package com.example.asinit_user.gdziejestczoper.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public interface GeocodeAddressCallback {

    void onSuccessGetAddress(JsonObject address);
    void onFailureGetAddress();
}
