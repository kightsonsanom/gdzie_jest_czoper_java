package pl.tolichwer.gdziejestczoper.ui.search;

import pl.tolichwer.gdziejestczoper.viewobjects.Geo;

public interface SearchFragmentViewModelCallback {

    void onStartDateSet(long dayToMillis);

    void onEndDateSet(long dayToMillis);

    void setLatestGeo(Geo geo);
}
