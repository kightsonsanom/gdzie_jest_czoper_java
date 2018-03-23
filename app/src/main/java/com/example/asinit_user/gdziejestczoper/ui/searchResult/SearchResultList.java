package com.example.asinit_user.gdziejestczoper.ui.searchResult;

import android.arch.lifecycle.ViewModelProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.asinit_user.gdziejestczoper.R;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class SearchResultList extends AppCompatActivity {



    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_list);




    }
}
