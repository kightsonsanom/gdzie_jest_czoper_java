package com.example.asinit_user.gdziejestczoper.ui.geoList;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;

import com.example.asinit_user.gdziejestczoper.db.Repository;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;
import com.example.asinit_user.gdziejestczoper.viewobjects.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class PositionListFragmentViewModel extends ViewModel {

    //pozycje, które obserwujemy na bazie
    private final LiveData<Resource<List<Position>>> mObservablePositions;

    // pozycja obserwowana z widoku
    public ObservableField<String> currentDay = new ObservableField<>();
    private Repository repository;

    @Inject
    public PositionListFragmentViewModel(Repository repository) {
        this.repository = repository;
        currentDay.set(getCurrentDay());

        //obserwujemy repozytorium i jeśli ulegnie zmianie to robimy update
        mObservablePositions = repository.getPositions();

    }

    public LiveData<Resource<List<Position>>> getObservablePositions() {
        return mObservablePositions;
    }




    private String getCurrentDay() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("dd MMM", new Locale("pl"));
        return format.format(date);
    }
}