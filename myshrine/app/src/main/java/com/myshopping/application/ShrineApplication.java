package com.myshopping.application;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.myshopping.network.ProductEntry;

import java.util.ArrayList;
import java.util.List;

public class ShrineApplication extends Application {

    public static List<ProductEntry> cartProductList;
    private static ShrineApplication instance;
    private static Context appContext;

    public static ShrineApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context mAppContext) {
        this.appContext = mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        this.setAppContext(getApplicationContext());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        cartProductList = new ArrayList<>();
    }

    public static void showLog(String msg) {
        Log.i("==App==", msg);
    }

    public static void showLog(String tag, String msg) {
        Log.i(tag + "==App==", msg);
    }

}