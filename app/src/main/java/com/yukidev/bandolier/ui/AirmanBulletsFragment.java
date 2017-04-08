package com.yukidev.bandolier.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.yukidev.bandolier.R;
import com.yukidev.bandolier.adapters.MessageAdapter;
import com.yukidev.bandolier.utils.ParseConstants;

import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * Created by James on 6/30/2015.
 */
public class AirmanBulletsFragment extends android.support.v4.app.ListFragment {

    private static final String TAG = AirmanBulletsFragment.class.getSimpleName();

    private ProgressBar mProgressBar;
    protected List<ParseObject> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected String mObjectId;
    private Boolean mDownload;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        Intent intent = getActivity().getIntent();
        mObjectId = intent.getStringExtra("objectId");
        mDownload = intent.getBooleanExtra("download", false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.inboxProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        return rootView;
    }

    protected void retrieveMessages() {

        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_SENDER_ID, mObjectId);
        query.whereEqualTo(ParseConstants.KEY_MESSAGE_TYPE, ParseConstants.MESSAGE_TYPE_BULLET);
        //if statement gets owners bullets from online
        if (!mDownload){
            query.fromPin(ParseConstants.CLASS_MESSAGES);
            try{
                mProgressBar.setVisibility(View.INVISIBLE);
            } catch (NullPointerException e) {
                // do nothing
            }
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {

                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if (e == null) {
                    //success
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mMessages = messages;
                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    try {
                        for (ParseObject message : mMessages) {
                            usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                            i++;
//                            if (getListView().getAdapter() == null) {
                            MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                            setListAdapter(adapter);
//                            } else {
//                                ((MessageAdapter) getListView().getAdapter()).refill(mMessages);
//                            }
                        }
                    } catch (ConcurrentModificationException ccm) {
                        Log.e(TAG, "ConcurrentMod Exception: " + ccm.getMessage());
                    }

                    ParseObject.pinAllInBackground(ParseConstants.CLASS_MESSAGES, messages);

                }

            }
        });
    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            // remember getActivity() for context
            Toast.makeText(getActivity(), "Retrieving messages . . .", Toast.LENGTH_SHORT).show();
            retrieveMessages();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        Intent intent;
//
//        switch (id) {
//            case R.id.action_write_bullet:
//                intent = new Intent(getActivity(), MessageActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.action_edit_account:
//                intent = new Intent(getActivity(), EditAccountActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.action_refresh_bullet:
//                retrieveMessages();
//                break;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        retrieveMessages();
//        this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("Delete Bullet?");
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(getActivity(), "Bullet deleted", Toast.LENGTH_LONG).show();
//                        ParseObject message = mMessages.get(position);
//                        message.deleteInBackground();
//                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
//                        view.setVisibility(View.GONE);
//                        adapter.remove(message);
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//                builder.setNegativeButton("CANCEL", null);
//                AlertDialog dialog = builder.create();
//                dialog.show();
//
//                return false;
//            }
//        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);

        Intent intent = new Intent(getActivity(), ViewMessageActivity.class);
        intent.putExtra(ParseConstants.KEY_OBJECT_ID, message.getObjectId());
        intent.putExtra(ParseConstants.LOCAL_STORAGE, false);
        startActivity(intent);
    }

    public void downloadMessages(){

        mProgressBar.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = new ParseQuery<>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_SENDER_ID, mObjectId);
        query.whereEqualTo(ParseConstants.KEY_MESSAGE_TYPE, ParseConstants.MESSAGE_TYPE_BULLET);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                if (e == null) {
                    //success
                    ParseObject.pinAllInBackground(ParseConstants.CLASS_MESSAGES, messages);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    mMessages = messages;
                    String[] usernames = new String[mMessages.size()];
                    int i = 0;
                    try {
                        for (ParseObject message : mMessages) {
                            usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                            i++;
                            if (getListView().getAdapter() == null) {
                                adapter = new MessageAdapter(getListView().getContext(), mMessages);
                                setListAdapter(adapter);
                            } else {
                                ((MessageAdapter) getListView().getAdapter()).refill(mMessages);
                            }
                        }
                    } catch (ConcurrentModificationException ccm) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Log.e(TAG, "ConcurrentMod Exception: " + ccm.getMessage());
                    }

                }
            }
        });
    }
}