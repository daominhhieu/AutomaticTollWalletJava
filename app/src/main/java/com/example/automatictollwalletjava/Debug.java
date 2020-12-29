package com.example.automatictollwalletjava;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.automatictollwalletjava.MyUtilities.MySocketHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class Debug extends Fragment {
    private final int PERMISSION_FINE_LOCATION = 2;
    private final int DEFAULT_INTERVAL_LOC_REQ = 30;
    private final int FAST_INTERVAL_LOC_REQ = 5;

    /**Google's API for location services ==> IMPORTANT!**/
    FusedLocationProviderClient fusedLocationProviderClient;
    /**Configuration for location services : "fusedLocationProviderClient"**/
    LocationRequest locationRequest = new LocationRequest();
    /**Get location**/
    LocationCallback locationCallback;
    TextView LLatitude_tv;

    ListView DebugLV;
    final String HOST = "192.168.1.17";
    final int PORT = 2312;
    MySocketHandler testConnection;
    String Skey = null;

    public Debug() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_debug, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button ConnectTCP = getActivity().findViewById(R.id.ConnectTCP);
        Button SendTCP = getActivity().findViewById(R.id.SendTCPbtn);
        Button GetTCP = getActivity().findViewById(R.id.GetTCP);
        Button GetConnectedDevice = getActivity().findViewById(R.id.GetPeerDevice);
        Button SwitchHotspot = getActivity().findViewById(R.id.SwitchP2P);

        DebugLV = getActivity().findViewById(R.id.debug_lv);
        LLatitude_tv = getActivity().findViewById(R.id.LLatitude_tv);
        /**This part is for declare actions in LoginActivity.java**/
        locationRequest.setInterval(1000* DEFAULT_INTERVAL_LOC_REQ);
        locationRequest.setFastestInterval(1000 * FAST_INTERVAL_LOC_REQ);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /**This part is to display GPS location**/
        LLatitude_tv.setText("NULL");

        final ArrayList<String> MessageList = new ArrayList<String>();
        final ArrayList<String> HostList = new ArrayList<String>();

        SwitchHotspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        GetConnectedDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ConnectTCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testConnection = new MySocketHandler();
            }
        });

        SendTCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,String> login_test = new HashMap<String,String>();
                login_test.put("action", "login");
                login_test.put("phone", "0835771741");
                login_test.put("password", "Hieu2312.");

                testConnection.StartWrite(login_test);
            }
        });

        GetTCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(testConnection.getBodyHash().size()>0)
//                {
//                    LogError("result:   "+testConnection.getBodyHash().get("result"));
//                }

            }


        });

        locationCallback =  new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LLatitude_tv.setText(String.valueOf(locationResult.getLastLocation().getLatitude()));
            }
        };
        setupGPS();

    }

    private void setupGPS()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        }
        else
        {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null)
                    {
                        LLatitude_tv.setText(String.valueOf(location.getLatitude()));
                    }

                }
            });
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void LogError(String string)
    {
        Log.d(TAG,this.getClass().getSimpleName() + " : " +  string);
    }

    private static ArrayList[] readArpCache()
    {
        ArrayList<String> ipList = new ArrayList<String>();
        ArrayList<String> maclist = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"), 1024);
            String line;
            while ((line = br.readLine()) != null) {
                Log.d(TAG  ,line);

                String[] tokens = line.split(" +");
                if (tokens.length >= 4) {
                    // verify format of MAC address
                    String macAddress = tokens[3];
                    if (macAddress.matches("..:..:..:..:..:..")) {
                        Log.d(TAG, "MAC=" + macAddress + " IP=" + tokens[0] + " HW=" + tokens[1]);

                        // Ignore the entries with MAC-address "00:00:00:00:00:00"
                        if (!macAddress.equals("00:00:00:00:00:00")) {

                            String ipAddress = tokens[0];
                            ipList.add(ipAddress);
                            maclist.add(macAddress);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert br != null;
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new ArrayList[]{ipList, maclist};
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                getActivity().finish();
            }
        }
    }
}