package pl.tolichwer.gdziejestczoper.ui.map;


import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pl.tolichwer.gdziejestczoper.R;
import pl.tolichwer.gdziejestczoper.databinding.MapFragmentViewBinding;

import com.google.android.gms.maps.MapView;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.DaggerFragment;
import timber.log.Timber;

public class MapFragment extends DaggerFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private MapView mapView;
    private MapFragmentViewBinding binding;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.map_fragment_view, container, false);
        mapView = binding.mapview;
        mapView.onCreate(savedInstanceState);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MapViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(MapViewModel.class);
        binding.setModel(viewModel);

        subscribeToModel(viewModel);
    }

    private void subscribeToModel(MapViewModel viewModel) {
        viewModel.getObservableGeo().observe(this, geos -> {
            if (geos != null && geos.data != null) {
                viewModel.setMapGeos(geos.data);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}