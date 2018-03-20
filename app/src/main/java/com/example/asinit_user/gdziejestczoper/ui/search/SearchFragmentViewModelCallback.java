package com.example.asinit_user.gdziejestczoper.ui.search;


import com.example.asinit_user.gdziejestczoper.db.entities.Position;

import java.util.List;

public interface SearchFragmentViewModelCallback {

    void onStartDateSet(long dayToMillis);

    void onEndDateSet(long dayToMillis);

    void setPositionForToday(List<Position> positionList);
}
