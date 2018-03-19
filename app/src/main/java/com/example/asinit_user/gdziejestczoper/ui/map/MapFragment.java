package com.example.asinit_user.gdziejestczoper.ui.map;


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

import com.example.asinit_user.gdziejestczoper.R;
import com.example.asinit_user.gdziejestczoper.databinding.MapFragmentViewBinding;
import com.example.asinit_user.gdziejestczoper.databinding.PositionListFragmentViewBinding;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class MapFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private MapViewModel viewModel;
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
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MapViewModel.class);
        binding.setModel(viewModel);


    }



}