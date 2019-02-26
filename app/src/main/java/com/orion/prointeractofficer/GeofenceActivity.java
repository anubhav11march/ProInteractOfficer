package com.orion.prointeractofficer;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class GeofenceActivity extends AppCompatActivity {
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private List<Geofence> geofencesList;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 42;
    private static final String TAG = GeofenceActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence);

        // log information that intent is created
        Log.i(TAG, "onCreate: ");

        // set intent to null
        geofencePendingIntent = null;

        // initialize geofence list
        geofencesList = new ArrayList<>();

        // add geofence
        geofencesList.add(getNewGeofence("geofence1", 37.422, -122.084, 100));

        // create geofence instance
        geofencingClient = getGeofencingClientInstance();

        addGeofence();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!checkPermissions()) {
            requestPermissions();
        } else {

        }
    }

    // setup geofence instance
    private GeofencingClient getGeofencingClientInstance() {
        return LocationServices.getGeofencingClient(this);
    }

    // create geofence objects
    private Geofence getNewGeofence(String geofenceId, double geofencePointLongitude, double geofencePointLatitude, float geofencePointRadius) {
        return new Geofence.Builder()
                .setRequestId(geofenceId)
                .setCircularRegion(geofencePointLongitude, geofencePointLatitude, geofencePointRadius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(60*1000)
                .build();
    }

    // setup initial triggers
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER | GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(geofencesList);
        return builder.build();
    }

    // geofence pending intent
    private PendingIntent getGeofencePendingIntent() {
        Log.i(TAG, "getGeofencePendingIntent: somewhere here");
        if (geofencePendingIntent != null) {
            Log.e(TAG, "getGeofencePendingIntent: not null");
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        intent.setAction("com.orion.prointeractofficer.GEOFENCE_TRIGGER");
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.i(TAG, "getGeofencePendingIntent: Returning intent");
        return geofencePendingIntent;
    }

    // add geofence
    private void addGeofence() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // yay! geofence added!
                        Log.d(TAG, "Geofence loaded");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // :(
                        Log.d(TAG, "Failed to load geofence with error\n" + e);
                    }
                });
    }

    // check location permissions
    private boolean checkPermissions() {
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        return permission == PackageManager.PERMISSION_GRANTED;
    }

    // request location permissions
    private void requestPermissions() {
        boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(shouldShowRationale) {
            showPermissionRequestAlertDialog();
        } else {
            Log.i(TAG, "requestPermissions: requesting permissions normally");
            callPermissionSystemDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: ");
        if(requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if(grantResults.length <= 0)  {
                Log.i(TAG, "onRequestPermissionsResult: something happened that should not");
            }  else if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if(!shouldShowRationale) {
                    AlertDialog permissionDialog = new AlertDialog.Builder(this)
                            .setTitle(R.string.location_permission_title)
                            .setMessage(R.string.location_permission_message)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, REQUEST_PERMISSIONS_REQUEST_CODE);
                                }
                            }).create();

                    permissionDialog.show();
                } else {
                    Log.i(TAG, "onRequestPermissionsResult: showing permission dialog");
                    showPermissionRequestAlertDialog();
                }
            }
        }
    }

    private void showPermissionRequestAlertDialog() {
        Log.i(TAG, "showPermissionRequestAlertDialog: showing permission dialog");
        AlertDialog permissionDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.location_permission_title)
                .setMessage(R.string.location_permission_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callPermissionSystemDialog();
                    }
                })
                .create();

        permissionDialog.show();
    }

    private void callPermissionSystemDialog() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }
}
