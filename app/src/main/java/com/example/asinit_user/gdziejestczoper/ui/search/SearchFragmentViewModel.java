package com.example.asinit_user.gdziejestczoper.ui.search;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;

import com.example.asinit_user.gdziejestczoper.db.Repository;
import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;
import com.example.asinit_user.gdziejestczoper.utils.Converters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import timber.log.Timber;

public class SearchFragmentViewModel extends ViewModel implements SearchFragmentViewModelCallback {

    public ObservableField<String> startDate = new ObservableField<>();
    public ObservableField<String> endDate = new ObservableField<>();
    public ObservableField<String> latestGeo = new ObservableField<>();
    private LiveData<List<Position>> observablePositions;

    private final MediatorLiveData<List<Position>> mObservablePositions;

    private Repository repository;
    private String searchToDay;
    private String searchFromDay;


    @Inject
    public SearchFragmentViewModel(Repository repository) {
        this.repository = repository;
        repository.setSearchFragmentViewModelCallback(this);

        mObservablePositions = new MediatorLiveData<>();
        mObservablePositions.setValue(null);
    }

    public LiveData<List<Position>> getObservablePositions() {
        return mObservablePositions;
    }

    @Override
    public void onStartDateSet(long dayToMillis) {
        searchFromDay = Converters.longToString(dayToMillis);
        startDate.set(dateFormat(dayToMillis));
    }

    @Override
    public void onEndDateSet(long dayToMillis) {
        searchToDay = Converters.longToString(dayToMillis);
        endDate.set(dateFormat(dayToMillis));
    }


    @Override
    public void setLatestGeo(Geo geo) {
        if (geo != null) {
            latestGeo.set(geo.getDisplayText());
        }

    }

    private String dateFormat(long dayToMillis) {
        Date data = new Date(dayToMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("pl"));
        return sdf.format(data);
    }


    public void getAllInfoFromDB() {
        List<Position> positions = repository.getAllPositions();
        List<Geo> geos = repository.getAllGeos();

        if (positions != null) {
            for (Position p : positions) {
                Timber.d("Pozycja: " + p);
            }
        }


        if (geos != null) {
            for (Geo g : geos) {
                Timber.d("Geo: " + g);
            }
        }

    }

    public void getAllPositions() {

        repository.getPositionsFromRange(searchFromDay, searchToDay);
    }

    @Override
    public void setObservablePositions(LiveData<List<Position>> observablePositions) {
        this.observablePositions = observablePositions;
        mObservablePositions.addSource(observablePositions, mObservablePositions::setValue);
    }

    public void getLatestGeo() {
        repository.getLatestGeoForTests();
    }
}
