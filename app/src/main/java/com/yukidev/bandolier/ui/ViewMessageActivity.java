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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yukidev.bandolier.R;
import com.yukidev.bandolier.utils.Crypto;
import com.yukidev.bandolier.utils.ParseConstants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ViewMessageActivity extends AppCompatActivity {

    @Bind(R.id.titleTextView)EditText mTitleText;
    @Bind(R.id.actionTextView)EditText mActionText;
    @Bind(R.id.resultTextView)EditText mResultText;
    @Bind(R.id.impactTextView)EditText mImpactText;
    @Bind(R.id.createdOnTextView)TextView mCreatedOnText;
    private ParseObject mOriginalMessage;
    private String mMessageId;
    private Boolean mStorage;
    private String mCreatedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mMessageId = intent.getStringExtra(ParseConstants.KEY_OBJECT_ID);
        mStorage = intent.getBooleanExtra(ParseConstants.LOCAL_STORAGE, false);

        try {

            mOriginalMessage = getMessage(mMessageId);

            String messageTitle = mOriginalMessage.getString(ParseConstants.KEY_BULLET_TITLE);
            String messageAction = mOriginalMessage.getString(ParseConstants.KEY_ACTION);
            String messageResult = mOriginalMessage.getString(ParseConstants.KEY_RESULT);
            String messageImpact = mOriginalMessage.getString(ParseConstants.KEY_IMPACT);
            mCreatedDate = mOriginalMessage.getString(ParseConstants.KEY_CREATED_ON);


            String decryptedAction = decryptThis(mOriginalMessage.getString(ParseConstants.KEY_SENDER_ID),
                    messageAction);
            String decryptedResult = decryptThis(mOriginalMessage.getString(ParseConstants.KEY_SENDER_ID),
                    messageResult);
            String decryptedImpact = decryptThis(mOriginalMessage.getString(ParseConstants.KEY_SENDER_ID),
                    messageImpact);


            mTitleText.setText(messageTitle);
            mActionText.setText(decryptedAction);
            mResultText.setText(decryptedResult);
            mImpactText.setText(decryptedImpact);
            mCreatedOnText.setText(mCreatedDate);

        }catch (ParseException e) {
            Toast.makeText(ViewMessageActivity.this,
                    "Problem retrieving message: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
        try {
            mOriginalMessage.put(ParseConstants.KEY_VIEWED, true);
            mOriginalMessage.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                    }
                }
            });
        } catch (NullPointerException npe) {
            finish();
        }

    }

    private ParseObject getMessage (String objectId) throws ParseException {
        ParseObject message;
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_MESSAGES);
        if (!mStorage) {
            message = query.fromLocalDatastore().get(objectId);
        } else {
            message = query.get(objectId);
        }

        return message;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete Bullet?");
                builder.setMessage("This will completely delete this bullet!  There is no Undo . . .");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mOriginalMessage.deleteInBackground();
                        Toast.makeText(ViewMessageActivity.this,
                                "Bullet deleted", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
                break;

            case R.id.action_save_bullet:
                updateMessage();
                break;
            case R.id.action_go_back:
                finish();
//                goBack();
                break;
        }

        return super.onOptionsItemSelected(item);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    protected void updateMessage() {

        String title;
        String action;
        String result;
        String impact;
        String pass = ParseUser.getCurrentUser().getObjectId();

        // prevents null object reference
        if (mActionText.getText().toString().equals("")){
            action = "empty";
        }else{
            action = mActionText.getText().toString();
        }

        if (mResultText.getText().toString().equals("")){
            result = "empty";
        }else{
            result = mResultText.getText().toString();
        }

        if (mImpactText.getText().toString().equals("")){
            impact = "empty";
        }else{
            impact = mImpactText.getText().toString();
        }

        if (mTitleText.getText().toString().equals("")){
            title = "empty";
        }else{
            title = mTitleText.getText().toString();
        }

        String encryptedAction = encryptThis(pass, action);
        String encryptedResult = encryptThis(pass, result);
        String encryptedImpact = encryptThis(pass, impact);

        mOriginalMessage.put(ParseConstants.KEY_BULLET_TITLE, title);
        mOriginalMessage.put(ParseConstants.KEY_ACTION, encryptedAction);
        mOriginalMessage.put(ParseConstants.KEY_RESULT, encryptedResult);
        mOriginalMessage.put(ParseConstants.KEY_IMPACT, encryptedImpact);


        if (!isNetworkAvailable()){
            mOriginalMessage.put(ParseConstants.KEY_BEEN_SENT, false);
            mOriginalMessage.pinInBackground();
            Toast.makeText(this, "Network unavailable, will upload bullet " +
                            "when connection is available",
                    Toast.LENGTH_LONG).show();
        } else {
            mOriginalMessage.put(ParseConstants.KEY_BEEN_SENT, true);
            mOriginalMessage.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(ViewMessageActivity.this,
                                "Message updated", Toast.LENGTH_LONG).show();
                    } else {
                        // problem sending, pin and send later
                        mOriginalMessage.put(ParseConstants.KEY_BEEN_SENT, false);
                        mOriginalMessage.pinInBackground();
                    }
                }
            });

        }
        finish();
//        goBack();
    }

    private void goBack(){
        Intent intent = new Intent(this, AirmanBulletsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("objectId", mMessageId);
        if (!isNetworkAvailable()) {
            intent.putExtra("download", false);
        } else {
            intent.putExtra("download", true);
        }
        startActivity(intent);
    }

}
