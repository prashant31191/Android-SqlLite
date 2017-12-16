package com.dynamicsqllite.utils;

/**
 * Created by prashant.patel on 5/11/2017.
 */
import android.location.Location;

public class CLocation extends Location {

    private boolean bUseMetricUnits = false;

    public CLocation(Location location)
    {
        this(location, true);
    }

    public CLocation(Location location, boolean bUseMetricUnits) {
        // TODO Auto-generated constructor stub
        super(location);
        this.bUseMetricUnits = bUseMetricUnits;
    }


    public boolean getUseMetricUnits()
    {
        return this.bUseMetricUnits;
    }

    public void setUseMetricunits(boolean bUseMetricUntis)
    {
        this.bUseMetricUnits = bUseMetricUntis;
    }

    @Override
    public float distanceTo(Location dest) {
        // TODO Auto-generated method stub
        float nDistance = super.distanceTo(dest);
        if(!this.getUseMetricUnits())
        {
            //Convert meters to feet
            nDistance = nDistance * 3.28083989501312f;
        }
        return nDistance;
    }

    @Override
    public float getAccuracy() {
        // TODO Auto-generated method stub
        float nAccuracy = super.getAccuracy();
        if(!this.getUseMetricUnits())
        {
            //Convert meters to feet
            nAccuracy = nAccuracy * 3.28083989501312f;
        }
        return nAccuracy;
    }

    @Override
    public double getAltitude() {
        // TODO Auto-generated method stub
        double nAltitude = super.getAltitude();
        if(!this.getUseMetricUnits())
        {
            //Convert meters to feet
            nAltitude = nAltitude * 3.28083989501312d;
        }
        return nAltitude;
    }

    @Override
    public float getSpeed() {
        // TODO Auto-generated method stub
        float nSpeed = super.getSpeed() * 3.6f;
        if(!this.getUseMetricUnits())
        {
            //Convert meters/second to miles/hour



           /* timeSeconds = (hr*3600) + (min*60) + sec;
            mps = distance / timeSeconds;
            kph = ( distance/1000.0f ) / ( timeSeconds/3600.0f );
            mph = kph / 1.609f;

            System.out.println("Your speed in meters/second is "+mps);
            System.out.println("Your speed in km/h is "+kph);
            System.out.println("Your speed in miles/h is "+mph);
            */

            nSpeed = nSpeed * 2.2369362920544f/3.6f; // Mile / H

            nSpeed = nSpeed * 1.609f; //KM / H
        }
        return nSpeed;
    }



}