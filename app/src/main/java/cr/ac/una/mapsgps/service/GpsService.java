package cr.ac.una.mapsgps.service;


import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;




import cr.ac.una.mapsgps.entidad.Localizacion;

public class GpsService extends IntentService {

    static private   LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationClient;


    public static final String ACTION_NEW_LOCATION =
            "cr.ac.una.ACTION_NEW_LOCATION";

    public GpsService() {
        super("GpsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getLocation();

    }


    @SuppressLint("MissingPermission")
    private Location getLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval( 5000);
        locationRequest.setFastestInterval( 5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult( LocationResult locationResult) {

                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Localizacion localizacion = new Localizacion(location.getLatitude(), location.getLongitude(), null);
                    Intent bcIntent = new Intent();
                    bcIntent.setAction(ACTION_NEW_LOCATION);
                    bcIntent.putExtra("localizacion", localizacion);
                    sendBroadcast(bcIntent);
                }
            };
        };


        fusedLocationClient.requestLocationUpdates( locationRequest, locationCallback,  null/* Looper */);
        Looper.loop();

        return null ;


    }


}