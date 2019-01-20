package com.example.asinit_user.gdziejestczoper.ui.login;


import com.example.asinit_user.gdziejestczoper.db.Repository;

import javax.inject.Inject;

public class LoginManager implements LoginManagerCallback{



    private LoginCallback loginCallback;
    private Repository repository;

    @Inject
    public LoginManager (Repository repository){
        this.repository = repository;
        repository.setLoginManagerCallback(this);
    }

    public void setLoginCallback(LoginCallback loginCallback) {
        this.loginCallback = loginCallback;
    }

    public void isUserLoggedIn(){
        repository.isUserLoggedIn();

    }

    public void getUsers(String login, String password){
        repository.getUsers(login,password);
    }

    @Override
    public void onLoginSuccess() {
//        getGeoAndPosForCurrentDay();
        loginCallback.showSuccess();
    }

//    private void getGeoAndPosForCurrentDay() {
//
//        repository.
//    }

    @Override
    public void onLoginFailure() {
        loginCallback.showError();
    }

    public void setMockUser() {
        repository.setMockUser();
    }
}
