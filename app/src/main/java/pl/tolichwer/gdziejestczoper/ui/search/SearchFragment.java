package pl.tolichwer.gdziejestczoper.ui.search;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import pl.tolichwer.gdziejestczoper.R;
import pl.tolichwer.gdziejestczoper.databinding.SearchFragmentBinding;
import pl.tolichwer.gdziejestczoper.ui.geoList.PositionsAdapter;
import pl.tolichwer.gdziejestczoper.utils.Constants;

import javax.inject.Inject;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.DaggerFragment;

public class SearchFragment extends DaggerFragment {

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
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
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
                itemsAdapter.addAll(resource.data);
            }
        });
        binding.userSpinner.setAdapter(itemsAdapter);
    }


    private void setOnClickListeners() {
        binding.startdateImageView.setOnClickListener((v) -> {
            datePickerFragment = DatePickerFragment.newInstance(Constants.START_DATE);
            datePickerFragment.setDatePickerCallback(viewModel);
            datePickerFragment.show(getChildFragmentManager(), getString(R.string.dialog_fragment_tag));
        });


        binding.stopdateImageView.setOnClickListener((v) -> {
            datePickerFragment = DatePickerFragment.newInstance(Constants.END_DATE);
            datePickerFragment.setDatePickerCallback(viewModel);
            datePickerFragment.show(getChildFragmentManager(), getString(R.string.dialog_fragment_tag));
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

    }
    private boolean checkRequiredFields() {
        return !binding.startdateEt.getText().toString().equals("") && !binding.stopdateEt.getText().toString().equals("") && binding.userSpinner.getSelectedItem() != null;
    }
}
