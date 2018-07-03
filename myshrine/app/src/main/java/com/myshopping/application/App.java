package com.myshopping.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.myshopping.network.ProductEntry;
import com.myshopping.utils.SharePreferences;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    public static String PREF_NAME = "com.myshopping";
    public static List<ProductEntry> cartProductList;

    private static App instance;
    private static SharePreferences sharePreferences;
    private static Context appContext;

    public static App getInstance() {
        return instance;
    }
    public static SharePreferences getSharePreferences() {
        return sharePreferences;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context mAppContext) {
        this.appContext = mAppContext;
    }

    @Override
    public void onCreate() {
        try {
            super.onCreate();
            instance = this;

            this.setAppContext(getApplicationContext());
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
            cartProductList = new ArrayList<>();
            sharePreferences = new SharePreferences(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void showLog(String msg) {
        Log.i("==App==", msg);
    }

    public static void showLog(String tag, String msg) {
        Log.i(tag + "==App==", msg);
    }



    public static void showToastLong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public static void showToastShort(Context context, String strMessage) {
        Toast.makeText(context, strMessage, Toast.LENGTH_SHORT).show();
    }


    public static void showSnackBar(View view, String strMessage) {
        try {
            Snackbar snackbar = Snackbar.make(view, strMessage, Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(Color.BLACK);
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void showSnackBarLong(View view, String strMessage) {
        Snackbar.make(view, strMessage, Snackbar.LENGTH_LONG).show();
    }
}