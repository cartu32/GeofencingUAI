package com.example.geofencing.Presenter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.geofencing.Views.Activities.MapsActivity;

public class MapsActivtyPresenter {
    private final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    private final int MULTIPLE_PERMISSON_REQUEST_CODE = 10003;
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 30;
    public static final long MIN_TIME_BW_UPDATES = 1000 * 45;

    /*Se declara una variable de tipo LocationManager encargada de proporcionar acceso al servicio de localización del sistema.*/
    private LocationManager locationManager;
    /*Se declara una variable de tipo Location que accederá a la última posición conocida proporcionada por el proveedor.*/
    private Location location;
    
    MapsActivity activity;

    public MapsActivtyPresenter(MapsActivity activity) {
        this.activity = activity;
    }

   // @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermisson()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                        activity.enableMap();
            } else
                {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION}, MULTIPLE_PERMISSON_REQUEST_CODE);
            }
        }
        else
        {
            activity.enableMap();
        }
    }



    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //We have the permission
            activity.showMessage("Ahora puedes agregar puntos de Georeferencia");
            activity.enableMap();
        } else {
            //We do not have the permission..
            activity.showMessage("El acceso a la ubicación en segundo plano es necesario para que se activen las geocercas...");
        }
    }



    public Location getLocation() {
        try {
            locationManager = (LocationManager) activity.getApplicationContext()
                    .getSystemService(activity.getApplicationContext().LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // Si no hay proveedor habilitado
                //solicito que active el gps
                activity.alertNoGps();
            }

            // if GPS Enabled get lat/long using GPS Services
             if (isGPSEnabled) {
                setPositionGPS();
            }else if (isNetworkEnabled) {
                setPositionNetwork();
            }

        } catch (Exception e) {
            Log.e("getLocation", e.getMessage());
        }
        return location;
    }

    public void setPositionGPS() {
        if (location == null) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) activity);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    activity.positionUpdate(location);
                }
            }
        }
    }

    void setPositionNetwork(){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) activity);
        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                activity.positionUpdate(location);
            }
        }

    }

}
