package com.example.geofencing.Views.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.geofencing.Interface.InterfaceConfigGeofence;
import com.example.geofencing.Presenter.GeofenceHelper;
import com.example.geofencing.Presenter.MapsActivtyPresenter;
import com.example.geofencing.R;
import com.example.geofencing.Views.Fragments.fragment_config_geofence;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, LocationListener, InterfaceConfigGeofence {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private float GEOFENCE_RADIUS_DEFAULT = 100;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";
    private AlertDialog  alert = null;

    private MapsActivtyPresenter mapsActivtyPresenter;
    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;
    private LatLng latLngClick=null;
    private Circle circle;

    /*Se declara una variable de tipo LocationManager encargada de proporcionar acceso al servicio de localización del sistema.*/
    private LocationManager locationManager;
    /*Se declara una variable de tipo Location que accederá a la última posición conocida proporcionada por el proveedor.*/
    private Location location;

    private fragment_config_geofence frag=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapsActivtyPresenter =new MapsActivtyPresenter(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
    }

    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        int resultCode= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog dlg =GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, RC_HANDLE_GMS);
            dlg.show();
        }

        mMap.setOnMapLongClickListener(this);

        mapsActivtyPresenter.checkPermisson();

    }

    public void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mapsActivtyPresenter.onRequestPermissionsResult(requestCode, permissions,grantResults);

    }



    public Location getLocation() {
        return mapsActivtyPresenter.getLocation();
    }


    @Override
    public void onLocationChanged(Location location) {
        positionUpdate(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void positionUpdate(Location location) {

        if (location != null) {
            //obtengo mi posicion
            this.location = location;
            float zoomLevel = 16.0f; //This goes up to 21
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        }
    }
    @SuppressLint("MissingPermission")
    public void enableMap(){
        //TODO preseteamos algunas herramientos que el mapa nos puede proveer
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        getLocation();
    }
    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });
    }

    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }


    private void addCircle(LatLng latLng, float radius) {

        this.circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .strokeColor(Color.argb(255, 255, 0,0))
                .fillColor(Color.argb(64, 255, 0,0))
                .radius(100)
                .strokeWidth(4));

    }
        @Override
        public void onMapLongClick(LatLng latLng) {
            handleMapLongClick(latLng);

        }

        private void handleMapLongClick(LatLng latLng) {
            latLngClick =latLng;

            mMap.clear();
            addMarker(latLng);
            addCircle(latLng, GEOFENCE_RADIUS_DEFAULT);

            frag = new fragment_config_geofence(this,GEOFENCE_RADIUS_DEFAULT);
            frag.show(getSupportFragmentManager(), fragment_config_geofence.class.getSimpleName());

        }

        public void alertNoGps() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                    .setCancelable(false)
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            mapsActivtyPresenter.setPositionGPS();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            alert = builder.create();
            alert.show();
        }
        @Override
        protected void onDestroy(){
            super.onDestroy();
            if(alert != null)
            {
                alert.dismiss ();
            }
        }

    @Override
    public void addGeofenceGraphic(float geofenceRadius) {
        addGeofence(latLngClick, geofenceRadius);

        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(frag.getId())).commit();
     }

    @Override
    public void clearGeofenceMaps() {
        mMap.clear();
        circle=null;
    }

    @Override
    public void updateCircleGraphic(float geofenceRadius) {
        circle.setRadius(geofenceRadius);
    }
}



    
