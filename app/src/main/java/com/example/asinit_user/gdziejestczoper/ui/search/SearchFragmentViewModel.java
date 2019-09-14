package com.example.asinit_user.gdziejestczoper.ui.search;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.location.Location;

import com.example.asinit_user.gdziejestczoper.db.Repository;
import com.example.asinit_user.gdziejestczoper.viewobjects.Geo;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;
import com.example.asinit_user.gdziejestczoper.utils.Converters;
import com.example.asinit_user.gdziejestczoper.viewobjects.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import javax.inject.Inject;

import timber.log.Timber;

public class SearchFragmentViewModel extends ViewModel implements SearchFragmentViewModelCallback {

    public ObservableField<String> startDate = new ObservableField<>();
    public ObservableField<String> endDate = new ObservableField<>();
    public ObservableField<String> latestGeo = new ObservableField<>();

    private  MediatorLiveData<TreeMap<String,List<Position>>> mObservablePositions;
    private final LiveData<Resource<List<String>>> observableUserNames;

    private Repository repository;
    //    private String searchToDay;
//    private String searchToDay;
    private long searchFromDay;
    private long searchToDay;
    private String userName;


    @Inject
    public SearchFragmentViewModel(Repository repository) {
        this.repository = repository;
        repository.setSearchFragmentViewModelCallback(this);

        mObservablePositions = new MediatorLiveData<>();
        mObservablePositions.setValue(null);
        observableUserNames = repository.getAllUsersNames();
    }

    public LiveData<TreeMap<String,List<Position>>> getObservablePositions() {
        return mObservablePositions;
    }

    @Override
    public void onStartDateSet(long dayToMillis) {
//        searchFromDay = Converters.longToString(dayToMillis);
        searchFromDay = dayToMillis;
        startDate.set(dateFormat(dayToMillis));
    }

    @Override
    public void onEndDateSet(long dayToMillis) {
//        searchToDay = Converters.longToString(dayToMillis);
        searchToDay = dayToMillis;
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

    public void getAllPositionsForUser() {
        mObservablePositions.addSource(repository.getPositionsFromRange(userName, searchFromDay, searchToDay), (result) -> mObservablePositions.setValue(result.data));
    }

    @Override
    public void setObservablePositions(LiveData<TreeMap<String, List<Position>>> observablePositions) {
//        mObservablePositions.addSource(observablePositions, mObservablePositions::setValue);
    }

    public void getLatestGeo() {
        repository.getLatestGeoForTests();
    }

    public void setNewLocation(Location location) {
        repository.setNewLocation(location);
    }

    public LiveData<Resource<List<String>>> getObservableUserNames() {
        return observableUserNames;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String displayError() {
        return repository.displayGeoJobIntentServiceError();
    }

    public void sendLog() {
        repository.sendLogFile();
    }

    public void deleteLog() {
        repository.deleteLogFile();
    }
}
