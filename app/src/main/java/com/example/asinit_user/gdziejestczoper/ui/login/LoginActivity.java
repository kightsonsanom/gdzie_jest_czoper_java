package com.example.asinit_user.gdziejestczoper.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Toast;


import com.example.asinit_user.gdziejestczoper.R;
import com.example.asinit_user.gdziejestczoper.databinding.ActivityLoginBinding;
import com.example.asinit_user.gdziejestczoper.ui.geoList.PositionListFragmentViewModel;
import com.example.asinit_user.gdziejestczoper.ui.mainView.NavigationActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import timber.log.Timber;

/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity {

    SharedPreferences preferences;
    String loggedIn = null;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ActivityLoginBinding binding;

    private LoginActivityViewModel viewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        Timber.d("Main activity onCreate");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginActivityViewModel.class);

        loggedIn = preferences.getString("userName", null);

        if (loggedIn!=null){
            Intent intent = new Intent(this, NavigationActivity.class);
            startActivity(intent);
            finish();
        }

        binding.usernameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    private boolean isEmailValid(String tempEmail) {
        return tempEmail.length() > 4;
    }

    private boolean isPasswordValid(String tempPassword) {
        return tempPassword.length() > 4;
    }

    private void attemptLogin() {


        // Reset errors.
        binding.username.setError(null);
        binding.password.setError(null);

        // Store values at the time of the login attempt.
        String username = binding.username.getText().toString();
        String userPassword = binding.password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(userPassword) && !isPasswordValid(userPassword)) {
            binding.password.setError(getString(R.string.error_invalid_password));
            focusView = binding.password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            binding.username.setError(getString(R.string.error_field_required));
            focusView = binding.username;
            cancel = true;

        } else if (!isEmailValid(username)) {
            binding.username.setError(getString(R.string.error_invalid_username));
            focusView = binding.username;
            cancel = true;
        }

        Timber.d("attempt login before asynctask");
        Log.d("TAG", "attempt login before asynctask");
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
//            viewModel.setLoginCallback(this);
            viewModel.getUsers(username, userPassword);

        }
    }


    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        binding.loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        binding.loginForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                binding.loginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
//
        binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.loginProgress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    //Po udanym zalogowaniu wrzucamy nazwę i ID użytkownika do sharedPreferences i odpalamy MainActivity
//    public void showSuccess(List<Czoper> czoperList) {
//        SharedPreferences.Editor editor = preferences.edit();
//        czoperManager.setAdmin(true);
//        editor.putString("userName", userLogin);
//        Timber.d("CzoperID w przy wsadzaniu do preferences= " + czoperList.get(0).getCzoperID());
//        editor.putInt("czoperID",czoperList.get(0).getCzoperID());
//        editor.apply();
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    @Override
//    public void showError() {
//        showProgress(false);
//        Toast.makeText(this, "Niepoprawne dane", Toast.LENGTH_SHORT).show();
//    }
}

