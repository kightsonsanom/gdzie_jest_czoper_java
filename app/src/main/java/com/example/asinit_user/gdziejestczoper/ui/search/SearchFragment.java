package com.example.asinit_user.gdziejestczoper.ui.search;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asinit_user.gdziejestczoper.R;
import com.example.asinit_user.gdziejestczoper.databinding.SearchFragmentBinding;
import com.example.asinit_user.gdziejestczoper.ui.geoList.PositionsAdapter;
import com.example.asinit_user.gdziejestczoper.ui.searchResult.SearchResultList;
import com.example.asinit_user.gdziejestczoper.utils.Constants;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

public class SearchFragment extends Fragment {


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    PositionsAdapter positionsAdapter;

    private DatePickerFragment datePickerFragment;
    private SearchFragmentViewModel viewModel;
    private SearchFragmentBinding binding;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        Timber.d("onAttach from SearchFragment");
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("onResume from SearchFragment");
        DatePickerFragment fragment = (DatePickerFragment) getChildFragmentManager().findFragmentByTag("dialogFragment");
        if (fragment != null) {
            fragment.updateListener(viewModel);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.search_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchFragmentViewModel.class);
        binding.setModel(viewModel);

        setOnClickListeners();


//        viewModel.getObservablePositions().observe(this, positions-> {
//            if (positions != null) {
//                positionsAdapter.setPositionsList(positions);
//            }
//        });

//        binding.positionRecycler.setAdapter(positionsAdapter);

        subscribeToModel(viewModel);
    }

    private void subscribeToModel(SearchFragmentViewModel viewModel) {

    }


    private void setOnClickListeners() {
        binding.startdateImageView.setOnClickListener((v) -> {
            datePickerFragment = DatePickerFragment.newInstance(Constants.START_DATE);
            datePickerFragment.setDatePickerCallback(viewModel);
            datePickerFragment.show(getChildFragmentManager(), "dialogFragment");
        });


        binding.stopdateImageView.setOnClickListener((v) -> {
            datePickerFragment = DatePickerFragment.newInstance(Constants.END_DATE);
            datePickerFragment.setDatePickerCallback(viewModel);
            datePickerFragment.show(getChildFragmentManager(), "dialogFragment");
        });

        binding.searchButton.setOnClickListener((v) -> {
            if (!binding.startdateEt.getText().toString().equals("") && !binding.stopdateEt.getText().toString().equals("")) {
                viewModel.getObservablePositions().observe(this, positions -> {
                    if (positions != null) {
                        ExpandableListAdapter expandableAdapter = new ExpandableListAdapter(getActivity());
                        expandableAdapter.setPositions(positions);
                        binding.positionsExpandable.setAdapter(expandableAdapter);
                    }
                });
                Timber.d("search button should work");
                viewModel.getAllPositions();
            }
        });


        binding.acceptGeoBtn.setOnClickListener((v) -> {

            String latitude = binding.latitude.getText().toString();
            String longitude = binding.longitude.getText().toString();
            Location location = new Location(LocationManager.GPS_PROVIDER);

            location.setLatitude(Double.parseDouble(latitude));
            location.setLongitude(Double.parseDouble(longitude));
            location.setTime(System.currentTimeMillis());
            viewModel.setNewLocation(location);

        });

//        binding.getPosBtn.setOnClickListener((v) -> {
//            viewModel.getLatestGeo();
//            viewModel.getAllInfoFromDB();
//
//        });
    }
}

