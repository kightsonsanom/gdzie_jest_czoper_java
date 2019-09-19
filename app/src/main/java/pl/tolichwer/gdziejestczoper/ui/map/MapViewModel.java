package pl.tolichwer.gdziejestczoper.ui.map;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.databinding.ObservableField;

import pl.tolichwer.gdziejestczoper.db.Repository;
import pl.tolichwer.gdziejestczoper.viewobjects.Geo;
import pl.tolichwer.gdziejestczoper.viewobjects.Resource;

import java.util.List;

import javax.inject.Inject;

public class MapViewModel extends ViewModel {


    public ObservableField<List<Geo>> latestGeoList = new ObservableField<>();
    private final MediatorLiveData<Resource<List<Geo>>> mObservableGeos;
    public int userID;

    @Inject
    MapViewModel(Repository repository) {

        mObservableGeos = new MediatorLiveData<>();
        mObservableGeos.setValue(null);

        LiveData<Resource<List<Geo>>> observableGeo = repository.getLatestGeoForUsers();
        mObservableGeos.addSource(observableGeo, value -> mObservableGeos.setValue(value));

        userID = repository.getUserID();
    }

    LiveData<Resource<List<Geo>>> getObservableGeo() {
        return mObservableGeos;
    }

    void setMapGeos(List<Geo> mapGeoList) {
        this.latestGeoList.set(mapGeoList);
    }

}
