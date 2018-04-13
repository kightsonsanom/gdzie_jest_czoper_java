package com.example.asinit_user.gdziejestczoper.ui.login;


import android.arch.lifecycle.ViewModel;

import com.example.asinit_user.gdziejestczoper.db.Repository;

import javax.inject.Inject;

public class LoginActivityViewModel extends ViewModel {



    private Repository repository;

    @Inject
    public LoginActivityViewModel(Repository repository){
        this.repository = repository;

    }


    public void getUsers(String username, String userPassword) {


    }
}
