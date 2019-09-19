package pl.tolichwer.gdziejestczoper.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import pl.tolichwer.gdziejestczoper.R;
import pl.tolichwer.gdziejestczoper.databinding.ActivityLoginBinding;
import pl.tolichwer.gdziejestczoper.ui.mainView.NavigationActivity;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class LoginActivity extends DaggerAppCompatActivity implements LoginCallback {

    @Inject
    LoginManager loginManager;

    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        loginManager.setLoginCallback(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginManager.isUserLoggedIn();

        binding.usernameSignInButton.setOnClickListener(view -> attemptLogin());

    }

    private void enterApplication() {
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
        finish();
    }


    private void attemptLogin() {

        binding.username.setError(null);
        binding.password.setError(null);

        String username = binding.username.getText().toString();
        String password = binding.password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (loginManager.isPasswordValid(password)) {
            binding.password.setError(getString(R.string.error_invalid_password));
            focusView = binding.password;
            cancel = true;

        } else if (loginManager.isUsernameValid(username)) {
            binding.username.setError(getString(R.string.error_invalid_username));
            focusView = binding.username;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            loginManager.getUsers(username, password);
        }
    }


    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        binding.usernameLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        binding.usernameLoginForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                binding.usernameLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
        binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.loginProgress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void showSuccess() {
        enterApplication();
    }

    @Override
    public void showError() {
        showProgress(false);
        Toast.makeText(this, R.string.incorrect_credentials, Toast.LENGTH_SHORT).show();
    }
}

