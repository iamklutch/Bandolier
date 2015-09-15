package com.yukidev.bandolier;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.yukidev.bandolier.utils.ParseConstants;

/**
 * Created by James on 9/8/2015.
 */
public class BandolierApplication extends Application {

    @Override
    public void onCreate() {
        // Enable Local Data  store.
        super.onCreate();
        // enable crash reporting
        ParseCrashReporting.enable(this);

        // enable local data storage
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "M1VXE37uOtP4z4P66nDFkDZK1FjkSF2Q3KbPOqKo", "PGYVQUZflvPv114MqirAhbdlm8U4qzHMLADiRUXI");

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }

}

