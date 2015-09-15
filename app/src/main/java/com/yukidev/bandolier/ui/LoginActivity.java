package com.yukidev.bandolier.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.yukidev.bandolier.BandolierApplication;
import com.yukidev.bandolier.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.loginProgressBar)ProgressBar mProgressBar;
    @Bind(R.id.loginEditText)EditText mUsername;
    @Bind(R.id.passwordEditText)EditText mPassword;
    @Bind(R.id.loginButton)Button mLoginButton;
    @Bind(R.id.signupText)TextView mSignUpTextView;
    @Bind(R.id.forgotPassText)TextView mForgotPassText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mProgressBar.setVisibility(View.INVISIBLE);

        if (isNetworkAvailable()) {

            mSignUpTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
            });

            mForgotPassText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    final EditText emailEditText = new EditText(LoginActivity.this);
                    emailEditText.setHint(R.string.forgot_password_email_hint);
                    builder.setTitle(R.string.forgot_email_dialog_title);
                    builder.setView(emailEditText);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            String email = emailEditText.getText().toString().toLowerCase().trim();
                            ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(LoginActivity.this,
                                                R.string.password_reset_email_sent_toast,
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(LoginActivity.this,
                                                getString(R.string.forgot_email_problem_toast_message) + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });

                    builder.setNegativeButton(R.string.cancel, null);
                    builder.create().show();
                }
            });

            mLoginButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String username = mUsername.getText().toString();
                    String password = mPassword.getText().toString();

                    username = username.trim().toLowerCase();
                    password = password.trim();

                    if (username.isEmpty() || password.isEmpty()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(R.string.login_error_message)
                                .setTitle(R.string.signup_error_title)
                                .setPositiveButton(android.R.string.ok, null);

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else {
                        //login
                        mProgressBar.setVisibility(View.VISIBLE);

                        ParseUser.logInInBackground(username, password, new LogInCallback() {
                            @Override
                            public void done(ParseUser user, ParseException e) {

                                if (e == null) {
                                    // success logging in
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    BandolierApplication.updateParseInstallation(user);

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage(e.getMessage())
                                            .setTitle(R.string.signup_error_title)
                                            .setPositiveButton(android.R.string.ok, null);

                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }

                        });
                    }
                }
            });
        } else {
            Toast.makeText(this, "Network Unavailable", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_on, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }
}
