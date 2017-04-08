package com.yukidev.bandolier.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.yukidev.bandolier.R;
import com.yukidev.bandolier.utils.Crypto;
import com.yukidev.bandolier.utils.ParseConstants;

import java.util.List;

public class AirmanBulletsActivity extends AppCompatActivity {

    private ParseUser mCurrentUser;
    private String mObjectId;
    protected List<ParseObject> mMessages;
    private String[] mTargetEmailAddress;
    private String mPrefEmailAddress;
    private SharedPreferences mPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airman_bullets);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new AirmanBulletsFragment())
                    .commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_airman_bullets, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent;

        switch (id) {
            case R.id.action_write_bullet:
                intent = new Intent(this, MessageActivity.class);
                startActivity(intent);
                break;
            case R.id.action_edit_account:
                intent = new Intent(this, EditAccountActivity.class);
                startActivity(intent);
                break;
            case R.id.action_email_bullets:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText emailAddress = new EditText(this);

                // ensures that if email is changed, correct email gets put in.
                mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                mPrefEmailAddress = mPreferences.getString(SettingsActivity.KEY_EMAIL, "");

                mTargetEmailAddress = new String[1];
                emailAddress.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                emailAddress.setHint(R.string.alertdialog_email_hint);
                if (mPreferences.getBoolean(SettingsActivity.KEY_EMAIL_CHECK, false)) {
                    emailAddress.setText(mPrefEmailAddress + ";");
                }
                builder.setTitle(R.string.alertdialog_email_title);
                builder.setMessage(getString(R.string.alertdialog_email_message));
                builder.setCancelable(true);
                builder.setView(emailAddress);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTargetEmailAddress[0] = emailAddress.getText().toString();
                        emailBullets();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
                break;
            case R.id.action_logout:
                mCurrentUser.logOutInBackground();
                finish();
                break;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void emailBullets(){
        mCurrentUser = ParseUser.getCurrentUser();
        mObjectId = mCurrentUser.getObjectId();

        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_SENDER_ID, mObjectId);
        query.whereEqualTo(ParseConstants.KEY_MESSAGE_TYPE, ParseConstants.MESSAGE_TYPE_BULLET);
        query.fromPin(ParseConstants.CLASS_MESSAGES);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                mMessages = messages;
                String ctAction, ctResult, ctImpact;
                String emailBody = "My bullets: \n";
                for (ParseObject message : mMessages) {
                    ctAction = decryptThis(mCurrentUser.getObjectId(),
                            message.get(ParseConstants.KEY_ACTION).toString());
                    ctResult = decryptThis(mCurrentUser.getObjectId(),
                            message.get(ParseConstants.KEY_RESULT).toString());
                    ctImpact = decryptThis(mCurrentUser.getObjectId(),
                            message.get(ParseConstants.KEY_IMPACT).toString());
                    emailBody = emailBody + message.get(ParseConstants.KEY_CREATED_ON) +
                            "\n" + message.get(ParseConstants.KEY_BULLET_TITLE).toString()
                            + "\nAction: " + ctAction + "\nResult: " + ctResult + "\nImpact: " +
                            ctImpact + "\n\n";
                }

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, mTargetEmailAddress);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Bullets from Bandolier User " +
                        mCurrentUser.getUsername());
                intent.putExtra(Intent.EXTRA_TEXT, emailBody);
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
    }

    private String decryptThis(String pass, String encryptedData) {
        String decryptedData = "";

        try {
            Crypto crypto = new Crypto(pass);
            decryptedData = crypto.decrypt(encryptedData);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return decryptedData;
    }

}
