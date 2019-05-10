package com.example.asinit_user.gdziejestczoper.ui.search;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.asinit_user.gdziejestczoper.R;
import com.example.asinit_user.gdziejestczoper.databinding.SearchFragmentBinding;
import com.example.asinit_user.gdziejestczoper.ui.geoList.PositionsAdapter;
import com.example.asinit_user.gdziejestczoper.ui.mainView.NavigationActivity;
import com.example.asinit_user.gdziejestczoper.utils.Constants;
import com.example.asinit_user.gdziejestczoper.utils.Converters;

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
        initUserSpinner();

    }

    private void initUserSpinner() {

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);

        viewModel.getObservableUserNames().observe(this, resource -> {
            if (resource != null && resource.data != null) {
//                itemsAdapter.add("Wybierz użytkownika");
                itemsAdapter.addAll(resource.data);
            }
        });
        binding.userSpinner.setAdapter(itemsAdapter);
//        binding.userSpinner.setSelection(0);
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
            if (checkRequiredFields()) {
                viewModel.setUserName((String) binding.userSpinner.getSelectedItem());
                viewModel.getObservablePositions().observe(this, positions -> {
                    if (positions != null) {
                        ExpandableListAdapter expandableAdapter = new ExpandableListAdapter(getActivity());
                        expandableAdapter.setPositions(positions);
                        binding.positionsExpandable.setAdapter(expandableAdapter);
                    }
                });
                viewModel.getAllPositionsForUser();
            }
        });

        binding.showErrorButton.setOnClickListener((v) -> {
            String error = viewModel.displayError();
            if (!error.equals("defaultString")) {
                binding.showErrorText.setText("jest exception!");
            }
        });


//         wywolanie przycisku, ktory symulowal pobranie nowej lokalizacji z GPS
        binding.acceptGeoBtn.setOnClickListener((v) -> {
//            viewModel.setNewLocation(((NavigationActivity) getActivity()).getLocation());
            Converters.readFromLogFile();
            viewModel.sendLog();
        });

        binding.getPosBtn.setOnClickListener((v) -> {
            viewModel.getLatestGeo();
            viewModel.getAllInfoFromDB();
        });
    }

    private boolean checkRequiredFields() {
        return !binding.startdateEt.getText().toString().equals("") && !binding.stopdateEt.getText().toString().equals("") && binding.userSpinner.getSelectedItem() != null;
    }
}
