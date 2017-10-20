package com.yukidev.bandolier.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.yukidev.bandolier.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
    Bugs to fix:
    2. prevent keyboard opening when viewing bullets

 */

public class LoginActivity extends AppCompatActivity {

    @BindView(com.yukidev.bandolier.R.id.loginProgressBar) ProgressBar mProgressBar;
    @BindView(com.yukidev.bandolier.R.id.loginEditText)EditText mUsername;
    @BindView(R.id.passwordEditText)EditText mPassword;
    @BindView(R.id.loginButton)Button mLoginButton;
    @BindView(R.id.signupText)TextView mSignUpTextView;
    @BindView(R.id.forgotPassText)TextView mForgotPassText;
    private FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mProgressBar.setVisibility(View.INVISIBLE);
        mFirebaseAuth = FirebaseAuth.getInstance();

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
                            mFirebaseAuth.sendPasswordResetEmail(email);
                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this,
                                    R.string.password_reset_email_sent_toast,
                                    Toast.LENGTH_LONG).show();
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

                        mFirebaseAuth.signInWithEmailAndPassword(username, password)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                            builder.setMessage(task.getException().getMessage())
                                                    .setTitle(R.string.signup_error_title)
                                                    .setPositiveButton(android.R.string.ok, null);
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });

                    }
                }
            });
        } else {
            Toast.makeText(this, "Network Unavailable", Toast.LENGTH_LONG).show();
            mProgressBar.setVisibility(View.INVISIBLE);
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
