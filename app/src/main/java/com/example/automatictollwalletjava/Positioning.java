package com.example.automatictollwalletjava;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Build;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnSuccessListener;
//
//import java.util.concurrent.Executor;
//
public class Positioning {
//    public static final int DEFAULT_INTERVAL_LOC_REQ = 30;
//    public static final int FAST_INTERVAL_LOC_REQ = 5;
//    /**Google's API for location services ==> IMPORTANT!**/
//    FusedLocationProviderClient fusedLocationProviderClient;
//    /**Configuration for location services : "fusedLocationProviderClient"**/
//    LocationRequest locationRequest;
//
//    LocationCallback locationCallback;
//    public Positioning()
//    {
//        locationRequest = new LocationRequest();
//        locationRequest.setInterval(1000* DEFAULT_INTERVAL_LOC_REQ);
//        locationRequest.setFastestInterval(1000 * FAST_INTERVAL_LOC_REQ);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
//
//    public interface Get_pos_interface {
//
//    }
//
//    public void Get_pos(AppCompatActivity appCompatActivity, final TextView LLatitude)
//    {
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appCompatActivity);
//        if(ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
//        {
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
//            locationCallback = new LocationCallback(){
//                @Override
//                public void onLocationResult(LocationResult locationResult) {
//                    super.onLocationResult(locationResult);
//                    if(locationResult != null){
//                        LLatitude.setText(locationResult.getLastLocation().toString());
//                    }
//                    else
//                    {
//                        LLatitude.setText("UNKNOWN");
//                    }
//                }
//            };
//        }
//        else
//        {
//            appCompatActivity.finish();
//        }
//
//    }
//
//
}
