package com.example.asinit_user.mvvmapplication.ui.geoList;


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

import com.example.asinit_user.mvvmapplication.R;
import com.example.asinit_user.mvvmapplication.databinding.PositionListFragmentViewBinding;
import com.example.asinit_user.mvvmapplication.ui.mainView.GeoAdapter;
import com.example.asinit_user.mvvmapplication.ui.mainView.PositionsAdapter;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class PositionListFragment extends Fragment {


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    PositionsAdapter positionsAdapter;

    @Inject
    GeoAdapter geoAdapter;


    private PositionListFragmentViewModel viewModel;
    private PositionListFragmentViewBinding binding;


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


    }
}
