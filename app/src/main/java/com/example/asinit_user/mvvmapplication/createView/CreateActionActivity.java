package com.example.asinit_user.mvvmapplication.createView;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.asinit_user.mvvmapplication.R;
import com.example.asinit_user.mvvmapplication.ViewModelFactory;
import com.example.asinit_user.mvvmapplication.databinding.ActivityCreateActionBinding;
import com.example.asinit_user.mvvmapplication.db.entities.ActionEntity;

import javax.inject.Inject;

public class CreateActionActivity extends AppCompatActivity {


    ActivityCreateActionBinding actionBinding;
    CreateActionViewModel viewModel;
    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBinding = DataBindingUtil.setContentView(this,R.layout.activity_create_action);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CreateActionViewModel.class);

        actionBinding.acceptAction.setOnClickListener(v -> {
            String actionText = String.valueOf(actionBinding.actionEt.getText());
            if (actionText != null){
                ActionEntity actionEntity = new ActionEntity(1, actionText);
                viewModel.postAction(actionEntity);
            }
        });
    }
}
