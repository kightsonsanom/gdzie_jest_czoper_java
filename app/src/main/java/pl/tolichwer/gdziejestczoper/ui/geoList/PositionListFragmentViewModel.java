package pl.tolichwer.gdziejestczoper.ui.geoList;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.databinding.ObservableField;

import pl.tolichwer.gdziejestczoper.db.Repository;
import pl.tolichwer.gdziejestczoper.utils.Converters;
import pl.tolichwer.gdziejestczoper.viewobjects.Position;
import pl.tolichwer.gdziejestczoper.viewobjects.Resource;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class PositionListFragmentViewModel extends ViewModel {

    private LiveData<Resource<List<Position>>> mObservablePositions;
    private final LiveData<Resource<List<String>>> observableUserNames;

    public ObservableField<String> currentDay = new ObservableField<>();
    private Repository repository;

    @Inject
    PositionListFragmentViewModel(Repository repository) {
        this.repository = repository;
        currentDay.set(Converters.getCurrentDay());
        observableUserNames = repository.getAllUsersNames();
    }

    LiveData<Resource<List<Position>>> getPositionsForUserAndDay(int position) {
        String name = observableUserNames.getValue().data.get(position);

        long timeFrom = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000);
        long timeTo = System.currentTimeMillis() - (System.currentTimeMillis() % 86400000) + 86400000;

        return repository.getPostionsForUserAndDay(name, timeFrom, timeTo);
    }

    LiveData<Resource<List<String>>> getObservableUserNames() {
        return observableUserNames;
    }

}