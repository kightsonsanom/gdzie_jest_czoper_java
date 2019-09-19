package pl.tolichwer.gdziejestczoper.services;

import com.google.gson.JsonObject;

public interface GeocodeAddressCallback {

    void onSuccessGetAddress(JsonObject address);
    void onFailureGetAddress();
}
