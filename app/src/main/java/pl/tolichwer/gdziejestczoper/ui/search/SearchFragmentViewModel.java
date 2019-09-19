package pl.tolichwer.gdziejestczoper.ui.search;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.databinding.ObservableField;

import pl.tolichwer.gdziejestczoper.db.Repository;
import pl.tolichwer.gdziejestczoper.viewobjects.Geo;
import pl.tolichwer.gdziejestczoper.viewobjects.Position;
import pl.tolichwer.gdziejestczoper.viewobjects.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import javax.inject.Inject;

public class SearchFragmentViewModel extends ViewModel implements SearchFragmentViewModelCallback {

    public ObservableField<String> startDate = new ObservableField<>();
    public ObservableField<String> endDate = new ObservableField<>();
    public ObservableField<String> latestGeo = new ObservableField<>();

    private MediatorLiveData<TreeMap<String, List<Position>>> mObservablePositions;
    private final LiveData<Resource<List<String>>> observableUserNames;

    private Repository repository;
    private long searchFromDay;
    private long searchToDay;
    private String userName;


    @Inject
    SearchFragmentViewModel(Repository repository) {
        this.repository = repository;
        repository.setSearchFragmentViewModelCallback(this);

        mObservablePositions = new MediatorLiveData<>();
        mObservablePositions.setValue(null);
        observableUserNames = repository.getAllUsersNames();
    }

    LiveData<TreeMap<String, List<Position>>> getObservablePositions() {
        return mObservablePositions;
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

    void getAllPositionsForUser() {
        mObservablePositions.addSource(repository.getPositionsFromRange(userName, searchFromDay, searchToDay), (result) ->
                mObservablePositions.setValue(result.data));
    }

    LiveData<Resource<List<String>>> getObservableUserNames() {
        return observableUserNames;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

}
