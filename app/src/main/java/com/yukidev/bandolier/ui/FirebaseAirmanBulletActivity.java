package com.yukidev.bandolier.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.yukidev.bandolier.R;
import com.yukidev.bandolier.utils.Crypto;
import com.yukidev.bandolier.utils.ParseConstants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IamKl on 3/13/2017.
 */

public class FirebaseAirmanBulletActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String mUserId;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private SharedPreferences mPreferences;
    private String mPrefEmailAddress;
    private String[] mTargetEmailAddress;
    private String mEmailBody;
    private ArrayList<HashMap<String, String>> mBulletMap;
    private int mInc;
    private Query mQueryRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_bullets);
        mInc = 0;
        mEmailBody = "";

        // Initialize Firebase Auth and Database Reference
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mBulletMap = new ArrayList<>();

        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
           mUserId = mFirebaseUser.getUid();

            // Set up ListView
            final ListView listView = (ListView) findViewById(R.id.listView);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
                    tv.setGravity(Gravity.CENTER);
                    tv.setBackgroundColor(0x999e9e9e);
                    return view;
                }
            };
            listView.setAdapter(adapter);
            listView.setLongClickable(true);

            // Use Firebase to populate the list.
            mDatabase.child("users").child(mUserId).child("bullets").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    adapter.add((dataSnapshot.child("date").getValue()
                            + "\n" + dataSnapshot.child("title").getValue()));
                    String decryptedAction = decryptThis(mUserId,dataSnapshot.child("action").getValue().toString());
                    String decryptedResult = decryptThis(mUserId, dataSnapshot.child("result").getValue().toString());
                    String decryptedImpact = decryptThis(mUserId, dataSnapshot.child("impact").getValue().toString());
                    HashMap<String, String> map = new HashMap<>();
                    map.put("encryptKey", mUserId);
                    map.put("bulletKey", dataSnapshot.getKey());
                    map.put("position", String.valueOf(mInc));
                    map.put("date", dataSnapshot.child("date").getValue().toString());
                    map.put("title", dataSnapshot.child("title").getValue().toString());
                    map.put("action", decryptedAction);
                    map.put("result", decryptedResult);
                    map.put("impact", decryptedImpact);
                    mBulletMap.add(mInc, map);
                    mInc = mInc + 1;
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

                    Boolean startRename = false;

                    for(int i=0;i < mBulletMap.size();i++){

                        HashMap<String, String> map = new HashMap<>();
                        map = mBulletMap.get(i);

                        if (previousChildName.equals(null)){
                            String decryptedAction = decryptThis(mUserId,
                                    dataSnapshot.child("action").getValue().toString());
                            String decryptedResult = decryptThis(mUserId,
                                    dataSnapshot.child("result").getValue().toString());
                            String decryptedImpact = decryptThis(mUserId,
                                    dataSnapshot.child("impact").getValue().toString());

                            HashMap<String, String> newMap = new HashMap<>();
                            newMap.put("encryptKey", mUserId);
                            newMap.put("bulletKey", dataSnapshot.getKey());
                            newMap.put("position", String.valueOf(mInc));
                            newMap.put("date", dataSnapshot.child("date").getValue().toString());
                            newMap.put("title", dataSnapshot.child("title").getValue().toString());
                            newMap.put("action", decryptedAction);
                            newMap.put("result", decryptedResult);
                            newMap.put("impact", decryptedImpact);
                            mBulletMap.add(mInc, newMap);
                            mBulletMap.remove(i);

                            String adapterTitle = adapter.getItem(i + 1);
                            adapter.remove(adapterTitle);
                            adapter.add((dataSnapshot.child("date").getValue()
                                    + "\n" + dataSnapshot.child("title").getValue()));
                            adapter.notifyDataSetChanged();
                            startRename = true;

                        }else if (map.get("bulletKey").equalsIgnoreCase(previousChildName)){

                            String decryptedAction = decryptThis(mUserId,
                                    dataSnapshot.child("action").getValue().toString());
                            String decryptedResult = decryptThis(mUserId,
                                    dataSnapshot.child("result").getValue().toString());
                            String decryptedImpact = decryptThis(mUserId,
                                    dataSnapshot.child("impact").getValue().toString());

                            HashMap<String, String> newMap = new HashMap<>();
                            newMap.put("encryptKey", mUserId);
                            newMap.put("bulletKey", dataSnapshot.getKey());
                            newMap.put("position", String.valueOf(mInc));
                            newMap.put("date", dataSnapshot.child("date").getValue().toString());
                            newMap.put("title", dataSnapshot.child("title").getValue().toString());
                            newMap.put("action", decryptedAction);
                            newMap.put("result", decryptedResult);
                            newMap.put("impact", decryptedImpact);

                            // changed index for mBulletMap from i +1 to mInc to make sure it goes to the end
                            mBulletMap.add((mInc), newMap);
                            mBulletMap.remove(i + 1);

                            String adapterTitle = adapter.getItem(i);
                            adapter.remove(adapterTitle);
                            adapter.add((dataSnapshot.child("date").getValue()
                                    + "\n" + dataSnapshot.child("title").getValue()));
                            adapter.notifyDataSetChanged();
                            startRename = true;

                        }

                        if (startRename = true){
                            map.put("position", String.valueOf(i));
                        }

                    }

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    adapter.remove((dataSnapshot.child("date").getValue()
                            + "\n" + dataSnapshot.child("title").getValue()));

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, String> map = new HashMap<>();
                    map = mBulletMap.get(position);
                    String encryptKey = mUserId;
                    String bulletKey = map.get("bulletKey");
                    String date = map.get("date");
                    String title = map.get("title");
                    String action = map.get("action");
                    String result = map.get("result");
                    String impact = map.get("impact");
                    viewMessage(encryptKey, bulletKey, date, title, action, result, impact);

                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final int bulletPosition = position;
                    AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(FirebaseAirmanBulletActivity.this);
                    deleteBuilder.setTitle("Delete Bullet?");
                    deleteBuilder.setMessage("Do you really want to delete this bullet?  There is no undo . . .");
                    deleteBuilder.setCancelable(true);
                    deleteBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HashMap<String, String> map = new HashMap<>();
                            map = mBulletMap.get(bulletPosition);
                            String bulletKey = map.get("bulletKey");
                            mDatabase.child("users").child(mUserId).child("bullets").child(bulletKey).removeValue();
                            adapter.remove(map.get("date") + "\n" + map.get("title"));
                            adapter.notifyDataSetChanged();
                            mBulletMap.remove(bulletPosition);
                            mInc = mInc - 1;

                        }
                    });
                    deleteBuilder.setNegativeButton("Cancel", null);
                    deleteBuilder.create().show();
                    return true;
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_firebase_bullets, menu);
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
                        EmailBullets emailBullets = new EmailBullets();
                        emailBullets.execute();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
                break;
            case R.id.action_logout:
                mFirebaseAuth.getInstance().signOut();
                finish();
                break;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void viewMessage (String encryptKey, String bulletKey, String date, String title, String action, String result, String impact){

        Intent intent = new Intent(this, ViewMessageActivity.class);
        intent.putExtra(ParseConstants.KEY_USER_ID, encryptKey);
        intent.putExtra(ParseConstants.KEY_OBJECT_ID, bulletKey);
        intent.putExtra(ParseConstants.KEY_CREATED_ON, date);
        intent.putExtra(ParseConstants.KEY_BULLET_TITLE, title);
        intent.putExtra(ParseConstants.KEY_ACTION, action);
        intent.putExtra(ParseConstants.KEY_RESULT, result);
        intent.putExtra(ParseConstants.KEY_IMPACT, impact);
        startActivity(intent);

    }

        // New firebase version for email
    private class EmailBullets extends AsyncTask<String, Void, String> {


            @Override
            protected void onPreExecute(){

                mEmailBody = "";

            }


            @Override
            protected String doInBackground(String... params) {

                // Use HashMap to create email body of bullets
                int i;
                for (i=0; i < mBulletMap.size(); i++){
                    HashMap<String, String> map = new HashMap<>();
                    map = mBulletMap.get(i);
                    String date = map.get("date");
                    String title = map.get("title");
                    String action = map.get("action");
                    String result = map.get("result");
                    String impact = map.get("impact");

                    mEmailBody = mEmailBody + date + "  " + title + "\n"
                        + action + "\n" + result + "\n" + impact+ "\n \n";

                }

                return null;

            }

                @Override
            protected void onPostExecute(String result){

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, mTargetEmailAddress);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Work Bullets from Bandolier User");
                intent.putExtra(Intent.EXTRA_TEXT, mEmailBody);
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
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

}
