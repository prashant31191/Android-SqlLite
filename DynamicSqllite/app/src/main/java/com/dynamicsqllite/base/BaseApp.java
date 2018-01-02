package com.dynamicsqllite.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.dynamicsqllite.App;
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

        App.showLog("====BaseApp========onCreate===");
       /* FacebookSdk.sdkInitialize(getApplicationContext());
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }*/
    }

    @Override
    protected void attachBaseContext(Context base) {

        App.showLog("====BaseApp========attachBaseContext===");
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
