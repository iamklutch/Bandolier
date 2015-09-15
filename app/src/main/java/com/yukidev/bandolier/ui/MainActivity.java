package com.yukidev.bandolier.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yukidev.bandolier.R;
import com.yukidev.bandolier.utils.ParseConstants;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ParseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        mCurrentUser = ParseUser.getCurrentUser();

        if(mCurrentUser == null) {
            navigateToLogin();
        }
        else {
            messageUpdater();
            String clickedId = mCurrentUser.getObjectId().toString();
            Intent intent = new Intent(MainActivity.this, AirmanBulletsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("objectId", clickedId);
            if (!isNetworkAvailable()) {
                intent.putExtra("download", false);
            } else {
                intent.putExtra("download", true);
            }
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // sends unsent messages (due to no internet when the messages were written
    private void messageUpdater() {

        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // saves resources and allows the messages to update in the background
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                Thread.currentThread();
                ParseQuery<ParseObject> query2 = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
                query2.fromLocalDatastore();
                query2.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {

                            if (!isNetworkAvailable()) {
                                // no network so don't try to send message.
                                Log.e("MainActivity: ", "MessageUpdater: No network");
                            } else {
                                for (int i = 0; i < list.size(); i++) {
                                    ParseObject currentMessage = list.get(i);
                                    currentMessage.put(ParseConstants.KEY_BEEN_SENT, true);
                                    currentMessage.saveEventually(new SaveCallback() {
                                        @Override
                                        public void done(ParseException f) {
                                            if (f == null) {

                                            } else {
                                                Log.e("MainActivity: ", "KEY_BEEN_SENT failed" + f.getMessage());
                                            }
                                        }
                                    });
                                }
                            }

                        } else {
                            Log.e("MainActivity: ", "MessageUpdater: failed retrieving local messages");
                        }

                    }
                });
            }
        });
        sendThread.start();
    }
}
