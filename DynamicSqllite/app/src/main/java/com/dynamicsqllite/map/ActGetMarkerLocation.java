package com.dynamicsqllite.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
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
import com.dynamicsqllite.map.demo.MarkerManager;
import com.dynamicsqllite.map.demo.NetworkMarker;
import com.dynamicsqllite.map.model.CustomMarkerModel;
import com.dynamicsqllite.map.model.SearchRouteListModel;
import com.dynamicsqllite.map.model.SearchRouteListResponse;
import com.dynamicsqllite.utils.CustomAutoCompleteTextView;
import com.dynamicsqllite.utils.PlaceDetailsJSONParser;
import com.dynamicsqllite.utils.PlaceJSONParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by prashant.patel on 12/16/2017.
 */

public class ActGetMarkerLocation extends ActBase implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, GoogleMap.OnMyLocationButtonClickListener
    {
        private static String TAG = "==###===ActGetMarkerLocation===###==";

        private GoogleMap mMap;
        private GoogleApiClient mGoogleApiClient;
        private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        Context mContext;
        TextView mLocationMarkerText;
        ImageView ivCancel,ivMylocation;
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
        ivMylocation = _findViewById(R.id.ivMylocation);
        actvSearch.setThreshold(1);

        mContext = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setClickEvents();

       


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

    private void setApiData()
    {
        try{

            SearchRouteListResponse model = new SearchRouteListResponse();

            if (model.arrayListSearchRouteListModel != null && model.arrayListSearchRouteListModel.size() > 0) {
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(Marker arg0) {
                        App.showLog("=======Click on marker===");
                        String strRid = arg0.getSnippet();
                        App.showLog("======getSnippet===Add markers=strRid=" +strRid );


                        return true;
                    }

                });

                if(networkMarkerManager !=null) {
                    networkMarkerManager.clear();
                    networkMarkerManager.addMarkers(createNetworkMarkers(model.arrayListSearchRouteListModel));
                }
                else {
                    App.showLog("======networkMarkerManager==NULL=====");
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

        private void setClickEvents() {
        try{

            ivCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolChanegMap = true;
                    actvSearch.setText("");
                }
            });

            ////////// AUTOCOMPLETE TEXT VIEW - Keyboard SEARCH BUTTON CLICK /////////
            actvSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        //do Whatever you Want to do

                        actvSearch.setThreshold(1);
                        actvSearch.showDropDown();

                        App.hideSoftKeyboardMy(ActGetMarkerLocation.this);

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
                        App.showSnackBar(mLocationMarkerText, "Please search your address again.");

                    }

                    App.hideSoftKeyboardMy(ActGetMarkerLocation.this);
                }
            });



            ivMylocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Click action

                    try {
                        if (mMap != null) {
                            mMap.stopAnimation();
                            Location myloc = mMap.getMyLocation();
                            if (myloc != null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myloc.getLatitude(), myloc.getLongitude())));

                                strLat = "" + myloc.getLatitude();
                                strLong = "" + myloc.getLongitude();

                                App.showLog("000000====Lat : " + strLat + "," + "Long : " + strLong);

                                updateLocation(new LatLng(myloc.getLatitude(), myloc.getLongitude()), false);
                            }

                        }

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        App.showSnackBar(mLocationMarkerText,"Please enable GPS location.");
                    }

                }
            });

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        }


        private ArrayList<NetworkMarker> createNetworkMarkers(ArrayList<SearchRouteListModel> arrList) {


        /*final ArrayList<NetworkMarker> networkMarkers = new ArrayList<>(LOCATIONS.length);
        int i = 0;
        for (LatLng location : LOCATIONS) {
            //networkMarkers.add(new NetworkMarker(this, location, "http://3.bp.blogspot.com/-ktQ-8FfBn3w/UfoVgK4QUVI/AAAAAAAAAIk/f85h2mNzFAQ/s1600/I-Love-You-2013-Photography-HD-Wallpaper-1080x675.jpg"));
            networkMarkers.add(new NetworkMarker(this, location, strImagesList[i]));
            i = i + 1;
        }*/


            ArrayList<NetworkMarker> networkMarkers = new ArrayList<>();

            for(SearchRouteListModel searchRouteListModel : arrList)
            {
                CustomMarkerModel customMarkerModel = new CustomMarkerModel();
                customMarkerModel.uid = searchRouteListModel.uid;

                double latitude = Double.parseDouble(searchRouteListModel.st_lat);
                double longitude = Double.parseDouble(searchRouteListModel.st_long);
                LatLng location = new LatLng(latitude, longitude);

                customMarkerModel.latLng = location;
                customMarkerModel.imgUrl = App.strBaseUploadedPicUrl + searchRouteListModel.rimg;
                customMarkerModel.data = searchRouteListModel.rid;


                networkMarkers.add(new NetworkMarker(this, customMarkerModel));
            }


            //NetworkMarker
            return networkMarkers;
        }



        private void updateLocation(LatLng centerLatLng,boolean blnFilter) {



            if (centerLatLng != null) {

                strLat = "" + centerLatLng.latitude;
                strLong = "" + centerLatLng.longitude;
                App.showLog("updateLocation--9999------Lat : " + strLat + "," + "Long : " + strLong);

                App.showLog("===set Api call===");
                App.showLog("=========Add markers==");
                if (App.isInternetAvail(ActGetMarkerLocation.this)) {
                    //111 asyncGetSearchRoute(blnFilter);
                } else {
                    App.showSnackBar(mLocationMarkerText, getString(R.string.strNetError));
                }



            /*networkMarkerManager.clear();
            //   mMap.setOnMarkerClickListener(networkMarkerManager);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker arg0) {
                    App.showLog("=======Click on marker===");
                    App.showLog("=========Add markers==" + arg0);
                    App.showLog("=========Add markers==" + arg0.getTag());
                    App.showLog("=========Add markers==" + arg0.getSnippet());
                    return true;
                }

            });
            networkMarkerManager.addMarkers(createNetworkMarkers());*/
            } else {
                App.showLog("=====centerLatLng=null==");
            }
        }



        @Override
        public void onConnected(@Nullable Bundle bundle) {

            App.showLog(TAG + "=====onConnected=====");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }


            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                changeMap(mLastLocation);
                App.showLog(TAG, "ON connected");

            } else
                try {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            try {
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onConnectionSuspended(int i) {
            App.showLog(TAG + "=====onConnectionSuspended=====");
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            App.showLog(TAG + "=====onConnectionFailed=====");
        }


        protected synchronized void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        @Override
        public void onLocationChanged(Location location) {
            App.showLog(TAG + "=====onLocationChanged=====");
            try {
                if (location != null)
                    changeMap(location);
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onMyLocationButtonClick() {
            App.showLog(TAG + "=====onMyLocationButtonClick=====");
            if (mMap != null) {
                mMap.stopAnimation();
                Location myloc = mMap.getMyLocation();
                if (myloc != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myloc.getLatitude(), myloc.getLongitude())));

                    strLat = "" + myloc.getLatitude();
                    strLong = "" + myloc.getLongitude();

                    App.showLog("11111111====Lat : " + strLat + "," + "Long : " + strLong);
                }

            }
            return true;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            App.showLog(TAG, "========OnMapReady============");
            networkMarkerManager = new MarkerManager<>(googleMap);
            mMap = googleMap;
            //mMap.getUiSettings().setCompassEnabled(false);

       /* mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;


                mMap.clear();

                try {

                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);

                    startIntentService(mLocation);

                    strLat = "" + mCenterLatLong.latitude;
                    strLong = "" + mCenterLatLong.longitude;


                    updateLocation(new LatLng(mCenterLatLong.latitude, mCenterLatLong.longitude));
                    mLocationMarkerText.setText("Lat : " + strLat + "," + "Long : " + strLong);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                return;
            }
        }


        @Override
        protected void onResume() {
            App.showLog(TAG + "=====onResume=====");
            super.onResume();
            try {
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected() == false) {
                    mGoogleApiClient.connect();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        protected void onPause() {
            App.showLog(TAG + "=====onPause=====");
            super.onPause();
            try {

            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }


        private boolean checkPlayServices() {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if (resultCode != ConnectionResult.SUCCESS) {
                if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                            PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } else {
                    //finish();
                }
                return false;
            }
            return true;
        }


        private void changeMap(Location location) {

            App.showLog(TAG, "Reaching map" + mMap);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                return;
            }

            // check if map is created successfully or not
            if (mMap != null) {
                mMap.getUiSettings().setZoomControlsEnabled(false);
                LatLng latLong;


                latLong = new LatLng(location.getLatitude(), location.getLongitude());

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong).zoom(19f).tilt(70).build();

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

                strLat = "" + location.getLatitude();
                strLong = "" + location.getLongitude();

                App.showLog("333333333====Lat : " + strLat + "," + "Long : " + strLong);

                //11---
                updateLocation(new LatLng(location.getLatitude(), location.getLongitude()),false);
                mLocationMarkerText.setText("##--##Lat : " + strLat + "," + "Long : " + strLong);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }


        }



        //////////////////////////////////////////////////////////////////////////////////
        //////////////////// Google place api for get Suggestion//////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        private String getAutoCompleteUrl(String place) {
            String url = place;
            try {

                // Obtain browser key from https://code.google.com/apis/console
                String key = "key=" + App.GOOGLE_PLACE_API_KEY;

                // place to be be searched
                String input = "input=" + place.replace(" ", "%20");

                // place type to be searched
                String types = "types=establishment";

                // Sensor enabled
                String sensor = "sensor=false";

                // Building the parameters to the web service
                String parameters = input + "&" + types + "&" + sensor + "&" + key;

                // Output format
                String output = "json";

                // Building the url to the web service
                url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;
                App.showLog("===url===" + url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return url;
        }

        private String getPlaceDetailsUrl(String ref) {

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=" + App.GOOGLE_PLACE_API_KEY;

            // reference of place
            String reference = "reference=" + ref;

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = reference + "&" + sensor + "&" + key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/details/" + output + "?" + parameters;

            App.showLog("==Place detail url==" + url);

            return url;
        }

        private class DownloadTask extends AsyncTask<String, Void, String> {

            private int downloadType = 0;

            // Constructor
            public DownloadTask(int type) {
                this.downloadType = type;
            }

            @Override
            protected String doInBackground(String... url) {

                // For storing data from web service
                String data = "";

                try {
                    // Fetching the data from web service
                    data = downloadUrl(url[0]);
                } catch (Exception e) {
                    App.showLog("=====Background Task====" + e.toString());
                }
                return data;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    switch (downloadType) {
                        case PLACES:
                            // Creating ParserTask for parsing Google Places
                            placesParserTask = new ParserTask(PLACES);

                            // Start parsing google places json data
                            // This causes to execute doInBackground() of ParserTask class
                            placesParserTask.execute(result);

                            break;

                        case PLACES_DETAILS:
                            // Creating ParserTask for parsing Google Places
                            placeDetailsParserTask = new ParserTask(PLACES_DETAILS);

                            // Starting Parsing the JSON string
                            // This causes to execute doInBackground() of ParserTask class
                            placeDetailsParserTask.execute(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                App.showLog("====Exception while downloading url====" + e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

            int parserType = 0;

            public ParserTask(int type) {
                this.parserType = type;
            }

            @Override
            protected List<HashMap<String, String>> doInBackground(String... jsonData) {

                JSONObject jObject;
                List<HashMap<String, String>> list = null;

                try {
                    jObject = new JSONObject(jsonData[0]);

                    switch (parserType) {
                        case PLACES:
                            PlaceJSONParser placeJsonParser = new PlaceJSONParser();
                            // Getting the parsed data as a List construct
                            list = placeJsonParser.parse(jObject);
                            break;
                        case PLACES_DETAILS:
                            PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
                            // Getting the parsed data as a List construct
                            list = placeDetailsJsonParser.parse(jObject);
                    }

                } catch (Exception e) {
                    App.showLog("==ParserTask====Exception====", e.toString());
                }
                return list;
            }

            @Override
            protected void onPostExecute(List<HashMap<String, String>> result) {
                try {
                    switch (parserType) {
                        case PLACES:
                            String[] from = new String[]{"description"};
                            // int[] to = new int[]{android.R.id.text1};
                            int[] to = new int[]{R.id.textAutoComplete};

                            // Creating a SimpleAdapter for the AutoCompleteTextView
                            //SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);
                            SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, R.layout.dropdown, from, to);

                            // Setting the adapter
                            actvSearch.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            break;
                        case PLACES_DETAILS:
                            HashMap<String, String> hm = result.get(0);

                            if (hm != null && hm.size() > 0) {
                                dblPickuplat = Double.parseDouble(hm.get("lat"));
                                dblPickuplong = Double.parseDouble(hm.get("lng"));



                                App.showLog("====dblPickuplat====="+dblPickuplat);
                                App.showLog("====dblPickuplong====="+dblPickuplong);



                            /* // Getting reference to the SupportMapFragment of the activity_main.xml
                        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
                        fm.getMapAsync(ActPickUp.this);*/

                                LatLng point = new LatLng(dblPickuplat, dblPickuplong);

                                CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(point);
                                CameraUpdate cameraZoom = CameraUpdateFactory.zoomBy(12);

                                // Showing the user input location in the Google Map
                                mMap.moveCamera(cameraPosition);
                                mMap.animateCamera(cameraZoom);


                                App.showLog("====Selected place Lat/Long====" + dblPickuplong + " **//** " + dblPickuplong);



                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //  asyncGetEstimateRateTime(String.valueOf(dblPickuplat), String.valueOf(dblPickuplong));

                                        strLat = ""+dblPickuplat;
                                        strLong = ""+dblPickuplong;

                                        App.showLog("===click on search data=====Lat : " + strLat + "," + "Long : " + strLong);

                                        updateLocation(new LatLng(dblPickuplat, dblPickuplong), false);

                                    }
                                }, 1000);
                            } else {
                                App.showLog("==========No lat long found=========");
                            }


                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        protected void onDestroy() {
            App.showLog(TAG + "=====onDestroy=====");
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }

            mMap = null;
            App.showLog(TAG+"==#####=onDestroy=####==");
            super.onDestroy();
        }

        @SuppressLint("NewApi")
        @Override
        public void onBackPressed() {
            App.showLog(TAG + "=====onBackPressed=====");
            try {{

                    final Dialog dialog = new Dialog(ActGetMarkerLocation.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                    // Include dialog.xml file
                    dialog.setContentView(R.layout.popup_exit);

                    // set values for custom dialog components - text, image and button
                    TextView tvExitMessage = (TextView) dialog.findViewById(R.id.tvMessage);
                    TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
                    TextView tvOK = (TextView) dialog.findViewById(R.id.tvOk);

                    String strAlertMessageExit = "Are you sure you want to exit ?";
                    String strYes = "YES";
                    String strNo = "NO";

                    tvExitMessage.setText(strAlertMessageExit);
                    tvCancel.setText(strNo);
                    tvOK.setText(strYes);


                    dialog.show();

                    tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    tvOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            finish();
                            finishAffinity();
                            onBackPressed();

                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        
        
    }
