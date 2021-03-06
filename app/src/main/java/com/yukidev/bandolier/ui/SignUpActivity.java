package com.yukidev.bandolier.ui;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yukidev.bandolier.R;
import com.yukidev.bandolier.utils.Crypto;
import com.yukidev.bandolier.utils.DateHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {
//    @BindView(R.id.usernameField) EditText mUsername;
    @BindView(R.id.passwordEditText)EditText mPassword;
    @BindView(R.id.passwordEditText2)EditText mPassword2;
    @BindView(R.id.emailField)EditText mEmail;
    @BindView(R.id.signupButton)Button mSignUpButton;
    @BindView(R.id.signUpProgressBar)ProgressBar mProgressBar;
    private FirebaseAuth mFirebaseAuth;
    private String mUserId;
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        mProgressBar.setVisibility(View.INVISIBLE);
        mFirebaseAuth = FirebaseAuth.getInstance();

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isNetworkAvailable()) {

//                    String displayName = mUsername.getText().toString().trim();
//                    String username = mUsername.getText().toString().trim().toLowerCase();
                    String password = mPassword.getText().toString().trim();
                    String password2 = mPassword2.getText().toString().trim();
                    String email = mEmail.getText().toString().trim().toLowerCase();

                    if (password.isEmpty() || email.isEmpty() || password.length() < 8) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                        builder.setMessage(R.string.signup_error_message)
                                .setTitle(R.string.signup_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else if (!password.equals(password2)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                        builder.setMessage(R.string.password_error_message)
                                .setTitle(R.string.signup_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else {

                        // create new user
                        mProgressBar.setVisibility(View.VISIBLE);

                        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Write the welcome message
                                            mUserId = mFirebaseAuth.getCurrentUser().getUid();
                                            DateHelper today = new DateHelper();
                                            String date = today.DateChangerThreeCharMonth();

                                            String encryptedAction = encryptThis(mUserId,
                                                    getString(R.string.welcome_action_message));
                                            String encryptedResult = encryptThis(mUserId,
                                                    getString(R.string.welcome_impact_message));
                                            String encryptedImpact = encryptThis(mUserId,
                                                    getString(R.string.welcome_result_message));

                                            Bullet bullet = new Bullet("Welcome! Tap here. . .", date,
                                                    encryptedAction, encryptedResult, encryptedImpact);

                                            mDatabase = FirebaseDatabase.getInstance().getReference();

                                            mDatabase.child("users").child(mUserId).child("bullets")
                                                    .push().setValue(bullet);

                                            // start Bandolier if the account is created
                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                            builder.setMessage(task.getException().getMessage())
                                                    .setTitle(R.string.signup_error_title)
                                                    .setPositiveButton(android.R.string.ok, null);
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        }
                                    }
                                });

/*                        ParseUser newUser = new ParseUser();
                        newUser.setUsername(username);
                        newUser.setPassword(password);
                        newUser.setEmail(email);
                        newUser.put(ParseConstants.KEY_SUPERVISOR_ID, "none");
                        newUser.put(ParseConstants.KEY_LASTNAME, "none");
                        newUser.put(ParseConstants.KEY_SQUADRON, "none");
                        newUser.put(ParseConstants.KEY_DISPLAY_NAME, displayName);
                        newUser.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    // success creating user

                                    final ParseObject message =
                                            new ParseObject(ParseConstants.CLASS_MESSAGES);
                                    String mCurrentUser = ParseUser.getCurrentUser().getObjectId();
                                    message.put(ParseConstants.KEY_SENDER_ID,
                                            mCurrentUser);
                                    message.put(ParseConstants.KEY_SENDER_NAME, "Bandolier");
                                    message.put(ParseConstants.KEY_TARGET_USER,
                                            mCurrentUser);
                                    message.put(ParseConstants.KEY_BULLET_TITLE,
                                            "Welcome to Bandolier!");
                                    String encAction = encryptThis(mCurrentUser,
                                            getString(R.string.welcome_action_message));
                                    String encResult = encryptThis(mCurrentUser,
                                            getString(R.string.welcome_result_message));
                                    String encImpact = encryptThis(mCurrentUser,
                                            getString(R.string.welcome_impact_message));
                                    message.put(ParseConstants.KEY_ACTION, encAction);
                                    message.put(ParseConstants.KEY_RESULT, encResult);
                                    message.put(ParseConstants.KEY_IMPACT, encImpact);
                                    message.put(ParseConstants.KEY_SUPERVISOR_ID, mCurrentUser);
                                    message.put(ParseConstants.KEY_VIEWED, false);
                                    message.put(ParseConstants.KEY_BEEN_SENT, false);
                                    message.put(ParseConstants.KEY_REQUEST_TYPE, "empty");
                                    message.put(ParseConstants.KEY_MESSAGE_TYPE, "bullet");
                                    message.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                // success sending message
                                            } else {
                                                message.pinInBackground(ParseConstants.CLASS_MESSAGES);
                                            }
                                        }
                                    });

                                    mProgressBar.setVisibility(View.INVISIBLE);

                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                    builder.setMessage(e.getMessage())
                                            .setTitle(R.string.signup_error_title)
                                            .setPositiveButton(android.R.string.ok, null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }
                        });
*/
                    }
                } else {
                    Toast.makeText(SignUpActivity.this,
                            R.string.internet_connection_unavailable, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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

    private String encryptThis(String pass, String data) {
        String encryptedData = "";
        try {
            Crypto crypto = new Crypto(pass);
            encryptedData = crypto.encrypt(data);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return encryptedData;
    }
}
