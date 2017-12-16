package com.dynamicsqllite.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dynamicsqllite.ActBase;
import com.dynamicsqllite.App;
import com.dynamicsqllite.R;
import com.dynamicsqllite.utils.CustomAutoCompleteTextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.HashMap;

/**
 * Created by prashant.patel on 12/16/2017.
 */

public class ActGetMarkerLocation extends ActBase implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, GoogleMap.OnMyLocationButtonClickListener
    {
        private static String TAG = "==###===ActDashboard===###==";

        private GoogleMap mMap;
        private GoogleApiClient mGoogleApiClient;
        private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        Context mContext;
        TextView mLocationMarkerText;
        ImageView ivCancel;
        CustomAutoCompleteTextView actvSearch;

        // search edit text
        DownloadTask placesDownloadTask;
        DownloadTask placeDetailsDownloadTask;
        ParserTask placesParserTask;
        ParserTask placeDetailsParserTask;
        final int PLACES = 0;
        final int PLACES_DETAILS = 1;


        String strPickupAddress = "";
        double dblPickuplat = 0, dblPickuplong = 0;
        boolean boolChanegMap = true;

        String strLat = "10.0";
        String strLong = "20.0";

        // for the add marker with images
        private MarkerManager<NetworkMarker> networkMarkerManager;




        @Override
    protected int baseViewData() {
        return R.layout.act_get_marker_location;
    }

    @Override
    protected void baseSetData() {
        mLocationMarkerText = _findViewById(R.id.mLocationMarkerText);
        actvSearch = _findViewById(R.id.actvSearch);
        ivCancel = _findViewById(R.id.ivCancel);
        actvSearch.setThreshold(1);

        mContext = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setClickEvents();

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolChanegMap = true;
                actvSearch.setText("");
            }
        });


        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!App.isLocationEnabled(mContext)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();



        } else {
            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

    }

        private void setClickEvents() {
        try{

            ////////// AUTOCOMPLETE TEXT VIEW - Keyboard SEARCH BUTTON CLICK /////////
            actvSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        //do Whatever you Want to do

                        actvSearch.setThreshold(1);
                        actvSearch.showDropDown();

                        App.hideSoftKeyboardMy(ActDashboard.this);

                    }
                    return true;
                }
            });


            ////// AUTOCOMPLETE TEXT VIEW - TEXT CHANGE AND SET TO ADAPTER //////
            actvSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if(charSequence.length() > 0) {
                        // Creating a DownloadTask to download Google Places matching "s"
                        placesDownloadTask = new DownloadTask(PLACES);

                        // Getting url to the Google Places Autocomplete api
                        String url = getAutoCompleteUrl(charSequence.toString());

                        // Start downloading Google Places
                        // This causes to execute doInBackground() of DownloadTask class
                        placesDownloadTask.execute(url);

                        //adapter.getFilter().filter(charSequence);
                        //adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


            ///// Setting an item click listener for the AutoCompleteTextView dropdown list /////
            actvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                        long id) {

                    App.showLog("==actvSearch==Clicked==");

                    ListView lv = (ListView) arg0;
                    SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

                    HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);
                    App.showLog("11==* hm - hash map *==" + adapter.getItem(index));
                    App.showLog("22==* hm - hash map *==" + hm.toString());


                    // Creating a DownloadTask to download Places details of the selected place
                    placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);

                    // Getting url to the Google Places details api
                    String url = getPlaceDetailsUrl(hm.get("reference"));

                    // Start downloading Google Place Details
                    // This causes to execute doInBackground() of DownloadTask class
                    placeDetailsDownloadTask.execute(url);

                    if (hm.get("description") != null && hm.get("description").toString().length() > 0) {

                    /*if (pickupMarker != null) {
                        pickupMarker.remove();
                    }*/
                        strPickupAddress = hm.get("description").toString();
                        App.showLog("==clicked 1111==" + strPickupAddress);

                        actvSearch.setText(strPickupAddress);

                    } else {
                        App.showLog("==clicked 2222==" + hm.get("description"));
                        rlSearch.setVisibility(View.VISIBLE);
                        App.showSnackBar(tvTitle, "Please search your address again.");

                    }

                    App.hideSoftKeyboardMy(ActDashboard.this);
                }
            });


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {

        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public boolean onMyLocationButtonClick() {
            return true;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {

        }
    }
