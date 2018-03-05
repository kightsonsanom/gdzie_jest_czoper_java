package com.example.asinit_user.mvvmapplication.ui.mainView;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.asinit_user.mvvmapplication.R;
import com.example.asinit_user.mvvmapplication.ViewModelFactory;
import com.example.asinit_user.mvvmapplication.databinding.ActivityMainBinding;
import com.example.asinit_user.mvvmapplication.ui.createView.CreateActionActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity {

    @Inject
    ViewModelFactory viewModelFactory;

    private ActionsAdapter recyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        ActivityMainBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        MainViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);

        mBinding.setMainModel(viewModel);

        viewModel.initActions();

        recyclerAdapter = new ActionsAdapter();
//        recyclerAdapter.setActionList(viewModel.getActions());
        mBinding.mainRecycler.setAdapter(recyclerAdapter);
        viewModel.getActions().observe(this, actions -> {
            if (actions != null) {
                recyclerAdapter.setActionList(actions);
            }
        });

        mBinding.addActionBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateActionActivity.class));
            }
        );

    }
}
