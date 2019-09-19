package pl.tolichwer.gdziejestczoper.ui.login;


import android.text.TextUtils;

import pl.tolichwer.gdziejestczoper.db.Repository;

import javax.inject.Inject;

public class LoginManager implements LoginManagerCallback {


    private LoginCallback loginCallback;
    private Repository repository;

    @Inject
    public LoginManager(Repository repository) {
        this.repository = repository;
        repository.setLoginManagerCallback(this);
    }

    void setLoginCallback(LoginCallback loginCallback) {
        this.loginCallback = loginCallback;
    }

    void isUserLoggedIn() {
        repository.isUserLoggedIn();

    }

    void getUsers(String login, String password) {
        repository.getUsers(login, password);
    }

    @Override
    public void onLoginSuccess() {
        loginCallback.showSuccess();
    }


    @Override
    public void onLoginFailure() {
        loginCallback.showError();
    }

    boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) || password.length() >= 4;
    }

    boolean isUsernameValid(String username) {
        return !TextUtils.isEmpty(username) || username.length() >= 4;
    }
}
