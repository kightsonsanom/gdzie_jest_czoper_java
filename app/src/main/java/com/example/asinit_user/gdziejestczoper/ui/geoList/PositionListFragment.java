package com.example.asinit_user.gdziejestczoper.ui.geoList;


import android.arch.lifecycle.LiveData;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.asinit_user.gdziejestczoper.R;
import com.example.asinit_user.gdziejestczoper.databinding.PositionListFragmentViewBinding;
import com.example.asinit_user.gdziejestczoper.utils.Converters;
import com.example.asinit_user.gdziejestczoper.viewobjects.Position;

import com.example.asinit_user.gdziejestczoper.viewobjects.Resource;
import com.example.asinit_user.gdziejestczoper.viewobjects.User;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import timber.log.Timber;

public class PositionListFragment extends Fragment {


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    PositionsAdapter positionsAdapter;

    private PositionListFragmentViewModel viewModel;
    private PositionListFragmentViewBinding binding;
    private final Fragment currentFragment = this;
    private boolean userListVisible;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.position_list_fragment_view, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PositionListFragmentViewModel.class);
        binding.setModel(viewModel);

        initUserList();

        setEventListeners();
    }

    private void initUserList() {

        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);

        viewModel.getObservableUserNames().observe(this, resource -> {
            if (resource != null && resource.data != null) {
                itemsAdapter.addAll(resource.data);
            }
        });

        userListVisible = true;
        binding.userList.setAdapter(itemsAdapter);
    }


    private void setEventListeners() {

        binding.userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                binding.positionRecycler.setAdapter(positionsAdapter);
                LiveData<Resource<List<Position>>> positions = viewModel.getPositionsForUserAndDay(position);

                positions.observe(currentFragment, resource -> {
                    if (resource != null && resource.data != null) {
                        displayPositions(resource.data);
                        Converters.sortPositions(resource.data);
                        displayPositions(resource.data);
                        positionsAdapter.setPositionsList(resource.data);
                    }
                });

                binding.userList.setVisibility(View.GONE);
                binding.currentDay.setVisibility(View.VISIBLE);
                binding.positionRecycler.setVisibility(View.VISIBLE);
                userListVisible = false;
//                viewModel.onListClick(position);
            }
        });
    }

    public void returnToUserList(){
        binding.userList.setVisibility(View.VISIBLE);
        binding.currentDay.setVisibility(View.GONE);
        binding.positionRecycler.setVisibility(View.GONE);
        userListVisible = true;

    }

    private void displayPositions(List<Position> data) {
        Timber.d("before and after");
        for (Position p : data) {
            Timber.d(" position = " + p);
        }
    }

    public boolean isUserListVisible() {
        return userListVisible;
    }
}
