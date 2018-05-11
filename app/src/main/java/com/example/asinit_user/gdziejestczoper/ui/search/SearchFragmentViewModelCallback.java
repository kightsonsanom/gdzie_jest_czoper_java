package com.example.asinit_user.gdziejestczoper.ui.search;


import android.arch.lifecycle.LiveData;

import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public interface SearchFragmentViewModelCallback {

    void onStartDateSet(long dayToMillis);

    void onEndDateSet(long dayToMillis);

    void setLatestGeo(Geo geo);

    void setObservablePositions(LiveData<TreeMap<String, List<Position>>> observablePositions);
}
