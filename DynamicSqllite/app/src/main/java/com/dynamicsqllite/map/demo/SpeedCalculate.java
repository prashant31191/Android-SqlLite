package com.dynamicsqllite.map.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import com.dynamicsqllite.ActBase;
import com.dynamicsqllite.App;
import com.dynamicsqllite.R;
import com.dynamicsqllite.utils.CLocation;
import com.dynamicsqllite.utils.GPSTracker;
import com.dynamicsqllite.utils.IBaseGpsListener;

import java.util.Formatter;
import java.util.Locale;

public class SpeedCalculate  extends ActBase implements IBaseGpsListener {

    private static String TAG = "==SpeedCalculate==";

    private static final int HANDLER_DELAY = 1000*5;
    private static final int START_HANDLER_DELAY = 500;
    Handler handler;
    GPSTracker gpsTracker;


    @SuppressLint("MissingPermission")
    void onCreateData() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        this.updateSpeed(null);

        CheckBox chkUseMetricUntis = (CheckBox) this.findViewById(R.id.chkMetricUnits);
        chkUseMetricUntis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                SpeedCalculate.this.updateSpeed(null);
            }
        });

        gpsTracker = new GPSTracker(SpeedCalculate.this);



        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Location location = gpsTracker.getLocation();
                if(location != null)
                {
                         App.showLog("====location=====getLongitude==="+location.getLongitude());
                        App.showLog("====location=====getLatitude==="+location.getLatitude());

                    CLocation myLocation = new CLocation(location, useMetricUnits());
                    updateSpeed(myLocation);
                }


                handler.postDelayed(this, HANDLER_DELAY);
            }
        }, START_HANDLER_DELAY);


    }

    public void finish()
    {
        super.finish();
        System.exit(0);
    }

    int i=0;
    private void updateSpeed(CLocation location) {
        // TODO Auto-generated method stub
        i = i+1;
        App.showLog("====updateSpeed=====i==="+i);
   //     App.showLog("====location=====getLongitude==="+location.getLongitude());
    //    App.showLog("====location=====getLatitude==="+location.getLatitude());

        float nCurrentSpeed = 0;

        if(location != null)
        {
            location.setUseMetricunits(this.useMetricUnits());
            nCurrentSpeed = location.getSpeed();
            App.showLog(TAG + "==nCurrentSpeed==> " + nCurrentSpeed);
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        String strUnits = "miles/hour";
        if(this.useMetricUnits())
        {
            strUnits = "meters/second";
        }

        TextView txtCurrentSpeed = (TextView) this.findViewById(R.id.txtCurrentSpeed);
        txtCurrentSpeed.setText(strCurrentSpeed + " " + strUnits);
    }

    private boolean useMetricUnits() {
        // TODO Auto-generated method stub
        CheckBox chkUseMetricUnits = (CheckBox) this.findViewById(R.id.chkMetricUnits);
        return chkUseMetricUnits.isChecked();
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if(location != null)
        {
            CLocation myLocation = new CLocation(location, this.useMetricUnits());
            this.updateSpeed(myLocation);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGpsStatusChanged(int event) {
        // TODO Auto-generated method stub

    }


    @Override
    protected int baseViewData() {
        return R.layout.act_speed_calculation;
    }

    @Override
    protected void baseSetData() {
onCreateData();
    }
}

