package cr.ac.una.mapsgps;

import android.annotation.TargetApi;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cr.ac.una.mapsgps.database.AppDatabase;
import cr.ac.una.mapsgps.entidad.Localizacion;
import cr.ac.una.mapsgps.service.GpsService;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    GoogleMap mMap;
    AppDatabase db;


    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList<String> permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;



    GpsService gpsService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "localizacion")
                .allowMainThreadQueries().build();



        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        startService(new Intent(this, GpsService.class));

        IntentFilter filter = new IntentFilter();
        filter.addAction(GpsService.ACTION_NEW_LOCATION);
        ProgressReceiver rcv = new ProgressReceiver();
        registerReceiver(rcv, filter);


    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MapsActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // select de toda la tabla y hacer puntos
        List<Localizacion> localizaciones = db.localizacionDao().getAll();
        for (Localizacion loc : localizaciones) {
            LatLng punto = new LatLng(loc.getLatitud(), loc.getLongitud());
            mMap.addMarker(new MarkerOptions().position(punto).title("Fecha:"+loc.getFecha()));
            System.out.println(loc.getFecha());
        }



    }

    public class ProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GpsService.ACTION_NEW_LOCATION)) {
                Localizacion localizacion = (Localizacion) intent.getSerializableExtra("localizacion");
                Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(localizacion.getLongitud()) + "\nLatitude:" + Double.toString(localizacion.getLatitud()), Toast.LENGTH_SHORT).show();
                if ( mMap!= null ) {
                    LatLng punto = new LatLng(localizacion.getLatitud(), localizacion.getLongitud());
                    mMap.addMarker(new MarkerOptions().position(punto).title("Marker in Sydney"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(punto));
                    // Hacer el insert
                    db.localizacionDao().insert(localizacion);
                }

            }
        }
    }
}
