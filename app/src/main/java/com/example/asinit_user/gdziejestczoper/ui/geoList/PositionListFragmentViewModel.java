package com.example.asinit_user.gdziejestczoper.ui.geoList;


import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.asinit_user.gdziejestczoper.db.Repository;
import com.example.asinit_user.gdziejestczoper.utils.Converters;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;
import com.example.asinit_user.gdziejestczoper.viewobjects.Resource;
import com.example.asinit_user.gdziejestczoper.viewobjects.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import timber.log.Timber;

public class PositionListFragmentViewModel extends ViewModel {

    //pozycje, które obserwujemy na bazie
    private LiveData<Resource<List<Position>>> mObservablePositions;
    private final LiveData<Resource<List<String>>> observableUserNames;

    // pozycja obserwowana z widoku
    public ObservableField<String> currentDay = new ObservableField<>();
    private Repository repository;

    @Inject
    public PositionListFragmentViewModel(Repository repository) {
        this.repository = repository;
        currentDay.set(Converters.getCurrentDay());

        //obserwujemy repozytorium i jeśli ulegnie zmianie to robimy update

        observableUserNames = repository.getAllUsersNames();
    }

    public LiveData<Resource<List<Position>>> getPositionsForUserAndDay(int position) {
        String name = observableUserNames.getValue().data.get(position);
        long timeFrom = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000 );
        long timeTo = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000 ) + 86400000 ;


        mObservablePositions = repository.getPostionsForUserAndDay(name, timeFrom, timeTo);

        return mObservablePositions;
    }

    public LiveData<Resource<List<String>>> getObservableUserNames() {
        return observableUserNames;
    }


}