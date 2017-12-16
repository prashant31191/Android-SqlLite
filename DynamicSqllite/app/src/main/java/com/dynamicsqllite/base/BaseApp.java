package com.dynamicsqllite.base;

import android.app.Application;

import com.facebook.BuildConfig;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;

/**
 * Created by prashant.patel on 12/15/2017.
 */

public class BaseApp extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
    }
}
