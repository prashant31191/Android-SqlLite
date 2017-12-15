package com.dynamicsqllite;

import android.util.Log;

import com.dynamicsqllite.base.BaseApp;

/**
 * Created by prashant.patel on 12/15/2017.
 */

public class App extends BaseApp
{
    public static void showLog(String t, String m)
    {
        Log.i(t,m);
    }

    public static void showLog(String m)
    {
        Log.i("===App===",m);
    }
}
