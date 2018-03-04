package com.example.asinit_user.mvvmapplication.mainView;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.asinit_user.mvvmapplication.R;
import com.example.asinit_user.mvvmapplication.ViewModelFactory;
import com.example.asinit_user.mvvmapplication.createView.CreateActionActivity;
import com.example.asinit_user.mvvmapplication.databinding.ActivityMainBinding;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {


    private MainViewModel viewModel;
    @Inject
    ViewModelFactory viewModelFactory;

    private ActionsAdapter recyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);

        mBinding.setMainModel(viewModel);

        viewModel.initActions();

        recyclerAdapter = new ActionsAdapter();
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
