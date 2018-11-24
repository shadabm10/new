package gropherapp.gropher.com.gropherapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import gropherapp.gropher.com.gropherapp.R;


public class MapScreen extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnCameraIdleListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    String TAG = "map_screen";
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Marker marker;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    String lat1,lng1;
    String address,city,state,country,zip,f_address;
    LatLng place;
    TextView tv_done,tv_show_add;


    final int MY_PERMISSIONS_REQUEST_GPS = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_screen);

        tv_done = findViewById(R.id.tv_done);
        tv_show_add = findViewById(R.id.tv_show_add);
        tv_show_add.setVisibility(View.GONE);


        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);


        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("address", address);
                intent.putExtra("city", city);
              //  intent.putExtra("state", state);
                intent.putExtra("country", country);
            //    intent.putExtra("zip", zip);
                intent.putExtra("lat1", lat1);
                intent.putExtra("lng1", lng1);
                intent.putExtra("f_address", f_address);
                setResult(RESULT_OK, intent);
                finish();
            }
        });



        mapFrag.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap=googleMap;
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                buildGoogleApiClient();
                checkGPSEnabled();

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mGoogleMap.clear();
                        marker = mGoogleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(address)
                                //  .snippet("Snippet")
                                .draggable(true)
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.mipmap.pin_location)));

                        getLocation(latLng.latitude,latLng.longitude);

                        Log.d("address", address);

                    }
                });

            }
        });

    }
    private void checkGPSEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MapScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(MapScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                } else {
                    ActivityCompat.requestPermissions(MapScreen.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_GPS);
                }

            } else {
                getLatlng();
            }
        } else {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();

            } else {
                getLatlng();
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.enable_gps))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.putExtra("GPS", true);
                        startActivityForResult(intent, 1);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Initialize Google Play Services
        /*if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }


        mGoogleMap.setOnMarkerDragListener(this);*/




       /* LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Log.d(TAG, "onMapReady latitude: "+latitude);
        Log.d(TAG, "onMapReady longitude: "+longitude);

        getLocation(latitude,longitude);*/

        /*Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d(TAG, "onMapReady: "+location);
        if (location != null) {
            //Getting longitude and latitude
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            //moving the map to location

            Log.d(TAG, "latitude > "+latitude);
            Log.d(TAG, "longitude > "+longitude);
            getLocation(latitude,longitude);*/
        /*    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
            CameraUpdate zoom1 = CameraUpdateFactory.zoomTo(zoom);

            map.moveCamera(center);
            map.animateCamera(zoom1);
*/


    }

    public void getLatlng() {
      /*  if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }


        mGoogleMap.setOnMarkerDragListener(this);


*/


        if (mGoogleApiClient == null)
            buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationRequest = LocationRequest.create();
        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            Log.d(TAG, "onMapReady latitude: "+latitude);
            Log.d(TAG, "onMapReady longitude: "+longitude);

            getLocation(latitude, longitude);


        }

    }


    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mGoogleMap.clear();
        LatLng latLng= marker.getPosition();

       // double lat = location.getLatitude();
       // double lng = location.getLongitude();

        Log.d(TAG, "onMarkerDragEnd: "+latLng);
        getLocation(latLng.latitude,latLng.longitude);

    }

    @Override
    public void onCameraIdle() {
        Log.d(TAG, "setOnCameraIdleListener");
        // Cleaning all the markers.

       LatLng mPosition = mGoogleMap.getCameraPosition().target;

        Log.d(TAG, "onCameraIdle mPosition: "+mPosition);

        getLocation(mPosition.latitude,mPosition.longitude);

    }
    @Override
    public void onLocationChanged(final Location location) {
        /*mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
*//*
        mLastLocation = location;
        mGoogleMap.clear();
      //  LatLng latLng= mLastLocation.getLatitude();

        // double lat = location.getLatitude();
        // double lng = location.getLongitude();

        Log.d(TAG, "onMarkerDragEnd: "+location.getLatitude()+","+location.getLongitude());
        getLocation(location.getLatitude(),location.getLongitude());
*/


    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            getLatlng();
        }
       /* mLocationRequest = new LocationRequest();
        //mLocationRequest.setInterval(1000);
      //  mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }*/
      //  fusedLocationProviderApi.requestLocationUpdates(mGoogleApiClient,  locationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.location_permission_needed))
                        .setMessage(getResources().getString(R.string.location_permission))
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapScreen.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, getResources().getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public  void getLocation(final double lat, final double lng){

        String URL ="https://maps.googleapis.com/maps/api/geocode/json?latlng="
                +lat+","+lng+"&key="
                +getResources().getString(R.string.GEO_CODE_API_KEY);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                //Here response will be received in form of JSONObject

                Log.d(TAG,"Server response - "+response );

                try {

                     address = " ";


                    JSONArray results = response.getJSONArray("results");

                    for(int i = 0; i<results.length(); i++){
                        JSONObject obj = results.getJSONObject(i);

                        JSONArray address_components = obj.getJSONArray("address_components");
                      //  Log.d(TAG, "address_components: "+address_components.toString());
                        for(int j = 0; j<address_components.length(); j++){
                            JSONObject object = address_components.getJSONObject(j);

                            JSONArray types = object.getJSONArray("types");
                           // Log.d(TAG, "types: "+types.toString());
                            for (int k =0 ; k<types.length();k++ ){
                                String object_k = types.getString(k);

                                if(object_k.equals("establishment") ||
                                        object_k.equals("point_of_interest") ||
                                        object_k.equals("premise")){
                                    address = object.getString("long_name");
                                    Log.d(TAG, "address1: "+address);
                                    break;


                                }else if(object_k.equals("route")){
                                    if(address.isEmpty()){
                                        address = object.getString("long_name");
                                    }else
                                    {
                                        address = address + ", " + object.getString("long_name");
                                    }
                                    Log.d(TAG, "address2: "+address);
                                    break;
                                }else if(object_k.equals("sublocality") ||
                                        object_k.equals("sublocality_level_1")){
                                    if(address.isEmpty()){
                                        address = object.getString("long_name");
                                    }else {
                                        address = address + ", " + object.getString("long_name");
                                    }
                                    Log.d(TAG, "address3: "+address);
                                    break;
                                }else if(object_k.equals("locality")){
                                     city = object.getString("long_name");
                                    Log.d(TAG, "city: "+city);
                                    break;
                                }else if(object_k.equals("administrative_area_level_1")){
                                     state = object.getString("long_name");
                                    Log.d(TAG, "state: "+state);
                                    break;
                                }
                                else if(object_k.equals("country")){
                                     country = object.getString("long_name");
                                    Log.d(TAG, "country: "+country);
                                    break;
                                }
                                else if(object_k.equals("postal_code")){
                                     zip = object.getString("long_name");
                                    Log.d(TAG, "zip: "+zip);
                                    break;
                                }


                            }


                        }
                         f_address = obj.getString("formatted_address");
                        Log.d(TAG, "f_address: "+f_address);

                        JSONObject geometry = obj.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");

                         lat1 = String.valueOf(location.getDouble("lat"));
                         lng1 = String.valueOf(location.getDouble("lng"));
                        Log.d(TAG, "lat: "+lat1);
                        Log.d(TAG, "lng: "+lng1);

                        break;
                    }






                    place = new LatLng(lat, lng);
                  //  tv_show_add.setText(address);
                   // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place,16.0f));


                     marker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(place)
                            .title(address)
                            //  .snippet("Snippet")
                            .draggable(true)
                            .icon(BitmapDescriptorFactory
                            .fromResource(R.mipmap.pin_location)));



                    marker.showInfoWindow();


                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place,16.0f));
                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();

         //   getLatlng();
        }
    }

    protected synchronized void buildGoogleApiClient() {

        Log.d("buildGoogleApiClient", "buildGoogleApiClient: ");

        int status = getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName());

        if (status == PackageManager.PERMISSION_GRANTED) {
            mGoogleApiClient = new GoogleApiClient.Builder(MapScreen.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();


        }
    }

}
