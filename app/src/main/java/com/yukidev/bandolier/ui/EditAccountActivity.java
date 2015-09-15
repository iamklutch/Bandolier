package com.yukidev.bandolier.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yukidev.bandolier.R;
import com.yukidev.bandolier.utils.ParseConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditAccountActivity extends AppCompatActivity {

    @Bind(R.id.currentUsernameField)TextView mUsername;
    @Bind(R.id.currentUserLastName)EditText mLastName;
    @Bind(R.id.currentUserSquadron)EditText mSquadron;
    @Bind(R.id.currentEmail)EditText mCurrentEmail;
    @Bind(R.id.newPasswordField)EditText mNewPassA;
    @Bind(R.id.newPasswordField2)EditText mNewPassB;
    @Bind(R.id.changePasswordButton)Button mPassButton;
    private ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        ButterKnife.bind(this);

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
            case R.id.action_edit_account_save_changes:
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
            case R.id.action_cancel:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
