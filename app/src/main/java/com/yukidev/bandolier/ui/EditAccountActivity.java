package com.yukidev.bandolier.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yukidev.bandolier.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditAccountActivity extends AppCompatActivity {
/*
    @BindView(R.id.currentUsernameField) TextView mUsername;
    @BindView(R.id.currentUserLastName)EditText mLastName;
    @BindView(R.id.currentUserSquadron)EditText mSquadron;
    @BindView(R.id.currentEmail)EditText mCurrentEmail;
    @BindView(R.id.newPasswordField)EditText mNewPassA;
    @BindView(R.id.newPasswordField2)EditText mNewPassB;
    private ParseUser mCurrentUser;
*/
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @BindView(R.id.changePasswordButton)Button mPassButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        final String userEmail = mFirebaseUser.getEmail();

        mPassButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                mFirebaseAuth.sendPasswordResetEmail(userEmail);
                Toast.makeText(EditAccountActivity.this,
                        R.string.password_reset_email_sent_toast,
                        Toast.LENGTH_LONG).show();
/*
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, "yuki.developers@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Password reset for ");
                intent.putExtra(Intent.EXTRA_TEXT, mFirebaseUser.toString());
                startActivity(Intent.createChooser(intent, "Send Email"));
*/
            }
        });
/*
        mCurrentUser = ParseUser.getCurrentUser();

        mUsername.setText(mCurrentUser.get(ParseConstants.KEY_DISPLAY_NAME).toString());
        mLastName.setText(mCurrentUser.get(ParseConstants.KEY_LASTNAME).toString());
        mSquadron.setText(mCurrentUser.get(ParseConstants.KEY_SQUADRON).toString());
        mCurrentEmail.setText(mCurrentUser.getEmail());

        mPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPass = mNewPassA.getText().toString().trim();
                String newPassB = mNewPassB.getText().toString().trim();

                if (newPass.equals("") || newPassB.equals("")) {
                    Toast.makeText(EditAccountActivity.this,
                            getString(R.string.password_change_empty_toast),
                            Toast.LENGTH_LONG).show();
                } else if (newPass.length() < 8 || newPassB.length() < 8) {
                    Toast.makeText(EditAccountActivity.this,
                            getString(R.string.new_password_length_error_toast),
                            Toast.LENGTH_LONG).show();
                } else if (!newPass.equals(newPassB)) {
                    Toast.makeText(EditAccountActivity.this,
                            getString(R.string.password_error_message),
                            Toast.LENGTH_LONG).show();
                } else {
                    mCurrentUser.setPassword(newPass);
                    mCurrentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(EditAccountActivity.this,
                                        getString(R.string.password_changed_toast),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(EditAccountActivity.this,
                                        "There was an error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
            }
        });


*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
/*            case R.id.action_edit_account_save_changes:
                mCurrentUser.put(ParseConstants.KEY_LASTNAME, mLastName.getText().toString().toLowerCase());
                mCurrentUser.put(ParseConstants.KEY_DISPLAY_NAME, mLastName.getText().toString());
                mCurrentUser.put(ParseConstants.KEY_SQUADRON, mSquadron.getText().toString().toUpperCase());
                mCurrentUser.setEmail(mCurrentEmail.getText().toString());
                mCurrentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(EditAccountActivity.this,
                                    "Changes Saved!",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(EditAccountActivity.this,
                                    "There was a problem: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
*/
            case R.id.action_cancel:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
