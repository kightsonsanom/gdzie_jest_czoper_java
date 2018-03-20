package com.example.asinit_user.gdziejestczoper.ui.search;


import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;

import com.example.asinit_user.gdziejestczoper.db.Repository;
import com.example.asinit_user.gdziejestczoper.db.entities.Position;
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
    public ObservableField<String> positionFromToday = new ObservableField<>();

    private Repository repository;
    private long searchToDay;
    private long searchFromDay;


    @Inject
    public SearchFragmentViewModel(Repository repository) {
        this.repository = repository;
        repository.setSearchFragmentViewModelCallback(this);


    }


    @Override
    public void onStartDateSet(long dayToMillis) {
        searchFromDay = dayToMillis;
        startDate.set(dateFormat(dayToMillis));
    }

    @Override
    public void onEndDateSet(long dayToMillis) {
        searchToDay = dayToMillis;
        endDate.set(dateFormat(dayToMillis));
    }

    public void getPositionForToday() {
        Timber.d("getting pos with string: " + Converters.longToString(System.currentTimeMillis()));
        repository.getPositionForToday(Converters.longToString(System.currentTimeMillis()));
    }


    @Override
    public void setPositionForToday(List<Position> positionList) {

        if (positionList != null) {
            Timber.d("positionList from setPositionForToday = " + positionList.toString());

            if (positionList.size() > 0) {
                Timber.d("first item from positionlist : " + positionList.get(0).toString());
                positionFromToday.set(positionList.get(0).toString());
            }
        }else {
            Timber.d("positionlist is null from repo");
        }

    }


    private String dateFormat(long dayToMillis) {
        Date data = new Date(dayToMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", new Locale("pl"));

        return sdf.format(data);
    }
}
