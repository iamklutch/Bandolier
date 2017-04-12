package com.yukidev.bandolier.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yukidev.bandolier.R;
import com.yukidev.bandolier.utils.Crypto;
import com.yukidev.bandolier.utils.ParseConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewMessageActivity extends AppCompatActivity {

    @BindView(R.id.titleTextView)EditText mTitleText;
    @BindView(R.id.actionTextView)EditText mActionText;
    @BindView(R.id.resultTextView)EditText mResultText;
    @BindView(R.id.impactTextView)EditText mImpactText;
    @BindView(R.id.createdOnTextView)TextView mDateText;
//    private ParseObject mOriginalMessage;
//    private String mMessageId;
//    private Boolean mStorage;
    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mDate;
    private String mTitle;
    private String mAction;
    private String mResult;
    private String mImpact;
    private String mEncryptKey;
    private String mBulletKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);
        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserId = mFirebaseUser.getUid();

        Intent intent = getIntent();
        mEncryptKey = intent.getStringExtra(ParseConstants.KEY_USER_ID);
        mBulletKey = intent.getStringExtra(ParseConstants.KEY_OBJECT_ID);
        mDate = intent.getStringExtra(ParseConstants.KEY_CREATED_ON);
        mTitle = intent.getStringExtra(ParseConstants.KEY_BULLET_TITLE);
        mAction = intent.getStringExtra(ParseConstants.KEY_ACTION);
        mResult = intent.getStringExtra(ParseConstants.KEY_RESULT);
        mImpact = intent.getStringExtra(ParseConstants.KEY_IMPACT);

        mDateText.setText(mDate);
        mTitleText.setText(mTitle);
        mActionText.setText(mAction);
        mResultText.setText(mResult);
        mImpactText.setText(mImpact);

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
/*
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete Bullet?");
                builder.setMessage("This will completely delete this bullet!  There is no Undo . . .");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase.child("users").child(mUserId).child("bullets").child(mBulletKey).removeValue();
                        Toast.makeText(ViewMessageActivity.this,
                                "Bullet deleted", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
                break;
*/
            case R.id.action_save_bullet:
                updateMessage();
                break;
            case R.id.action_go_back:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
/*
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
*/
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
/*
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            isAvailable = true;
        }
        return isAvailable;
    }
*/
    protected void updateMessage() {

        String title;
        String action;
        String result;
        String impact;
//        DateHelper originalDate = new DateHelper();
//        String orderDate = originalDate.ThreeCharMonthToOrderDate(mDate);

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

        String encryptedAction = encryptThis(mEncryptKey, action);
        String encryptedResult = encryptThis(mEncryptKey, result);
        String encryptedImpact = encryptThis(mEncryptKey, impact);

        Bullet bullet = new Bullet(title, mDate, encryptedAction, encryptedResult, encryptedImpact);
        mDatabase.child("users").child(mUserId).child("bullets").child(mBulletKey).setValue(bullet);
        finish();

    }

}
