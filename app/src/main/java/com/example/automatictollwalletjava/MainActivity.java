package com.example.automatictollwalletjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.automatictollwalletjava.MyUtilities.MySocketHandler;
import com.example.automatictollwalletjava.add_retrieve_fragment.AddMoneyDialog;
import com.example.automatictollwalletjava.add_retrieve_fragment.RetrieveMoneyDialog;
import com.example.automatictollwalletjava.vehicle_interaction.DriverRegisterDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity implements AddMoneyDialog.AddMoneyFragment_inf,
        RetrieveMoneyDialog.RetrieveMoneyDialog_inf,
        DriverRegisterDialog.DriverRegisterDialog_inf{

    public BottomNavigationView bottomNavigationView;
    public static MySocketHandler MainSocketHandler = new MySocketHandler();
    public static String MainActivityPhone = "null";
    public static String MainActivityBudget = "null";
    public static String MainActivityVehicle = "null";
    public static int MainActivityVehicleMass = 0;

    private final int PERMISSION_FINE_LOCATION = 2;
    private final int DEFAULT_INTERVAL_LOC_REQ = 1;
    private final int FAST_INTERVAL_LOC_REQ = 1;

    public static Location Pos1st = null;
    public static Location Pos2nd = null;
    public static String StreetLast = null;



    /**Google's API for location services ==> IMPORTANT!**/
    FusedLocationProviderClient fusedLocationProviderClient;
    /**Configuration for location services : "fusedLocationProviderClient"**/
    LocationRequest locationRequest = new LocationRequest();
    /**Get location**/
    LocationCallback locationCallback;
    Geocoder loc_Geocoder;


    private HashMap<String, String> message = new HashMap<String, String>();
    public static BluetoothAdapter mBluetoothAdapter;
    public static ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public static MutableLiveData<ArrayList<BluetoothDevice>> mBTDevices_Mutate = new MutableLiveData<ArrayList<BluetoothDevice>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.general_nav_fragment);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        /**This part is for declare actions in LoginActivity.java**/
        locationRequest.setInterval(1000* DEFAULT_INTERVAL_LOC_REQ);
        locationRequest.setFastestInterval(1000 * FAST_INTERVAL_LOC_REQ);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        loc_Geocoder = new Geocoder(this, Locale.getDefault());
        locationCallback =  new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LogError("Location information:..."+locationResult.getLastLocation().toString());
                if(MainActivityVehicleMass > 0)
                {
                    try {

                        Location tmp_loc = locationResult.getLastLocation();
                        Address tmp_add = loc_Geocoder.getFromLocation(tmp_loc.getLatitude(), tmp_loc.getLongitude(), 1).get(0);
                        String tmp_street = null;
                        int tmp_idx = tmp_add.getMaxAddressLineIndex();
                        if(tmp_idx > -1)
                        {
                            tmp_street = tmp_add.getAddressLine(tmp_idx);
                            LogError("Street name:..."+tmp_street);
                        }

                        if(Pos1st == null)
                        {
                            Pos1st = tmp_loc;
                        }

                        if(StreetLast == null)
                        {
                            StreetLast = tmp_street;
                        }
                        else if(tmp_street != null)
                        {
                            if(!StreetLast.equals(tmp_street))
                            {
                                Pos2nd = tmp_loc;
                            }
                        }

                        if((Pos2nd != null) && (Pos1st != null))
                        {
                            pay_fee(Pos1st, Pos2nd, StreetLast);
                            Pos2nd = null;
                            Pos1st = Pos2nd;
                            StreetLast = tmp_street;
                        }

                    } catch (IOException e) {
                        LogError("Error:..."+e);
                    }

                }
                else
                {
                    ResetGPS();
                }


            }
        };
        setupGPS();
    }

    private void ResetGPS(){
        Pos1st = null;
        Pos2nd = null;
        StreetLast = null;
    }

    private void pay_fee(Location pos1, Location pos2, String street)
    {
        if((MainActivityPhone != null))
        {
            try{
                message.put("phone", MainActivityPhone);
                message.put("action", "payfee");
                message.put("Longitude1", String.valueOf(pos1.getLongitude()));
                message.put("Latitude1", String.valueOf(pos1.getLatitude()));
                message.put("Longitude2", String.valueOf(pos2.getLongitude()));
                message.put("Latitude2", String.valueOf(pos2.getLatitude()));
                message.put("street", street);

                MainSocketHandler.StartWrite(message);
            }catch (Exception e){
                LogError("Exception:..."+e);
            }
            return;
        }
        ShowToast("Connection Error");
    }

    @Override
    public void AddAmount(String money) {
        if((MainActivityPhone != null)&& (money != null))
        {
            try{
                message.put("phone", MainActivityPhone);
                message.put("action", "addmoney");
                message.put("money", money);
                MainSocketHandler.StartWrite(message);
            }catch (Exception e){
                LogError("Exception:..."+e);
            }
            LogError("Money Amount:..."+money);
            return;
        }
        LogError("Failed:..."+money);
        ShowToast("Connection Error");
    }

    @Override
    public void RetrieveAmount(String money) {
        if((MainActivityPhone != null) && (MainActivityBudget != null) && (money != null))
        {
            if(Integer.parseInt(money) <= Integer.parseInt(MainActivityBudget))
            {
                try {
                    message.put("phone", MainActivityPhone);
                    message.put("action", "retrievemoney");
                    message.put("money", money);
                    MainSocketHandler.StartWrite(message);
                }catch (Exception e){
                    LogError("Exception:..."+e);
                }
                LogError("Money Amount:..."+money);
                return;
            }
            LogError("Failed:..."+money);
            ShowToast("Invalid amount of money");
            return;
        }
        LogError("Failed:..."+money);
        ShowToast("Connection Error");

    }

    private void LogError(String string)
    {
        Log.d(TAG,this.getClass().getSimpleName() + " : " +  string);
    }

    @Override
    public void DriverReg(DialogFragment drv_reg) {

    }

    private void setupGPS()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        }
        else
        {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null)
                    {

                    }

                }
            });
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }


    /**This function is show toast**/
    private void ShowToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


}