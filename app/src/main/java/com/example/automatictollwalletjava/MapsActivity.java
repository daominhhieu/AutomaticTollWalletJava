package com.example.automatictollwalletjava;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Polyline line;
    private MarkerOptions Place1, Place2;
    public static int MESSAGE_KEY =0;
    public static int FEE_KEY = 0;
    int fee = 0;
    double distance = 0;

    ArrayList<double[]> place = new TestStoredFunction().coordinates_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TestStoredFunction.Track_profile track = new TestStoredFunction.Track_profile(true);
        track.start_coord = TestStoredFunction.coordinates_set.get(MESSAGE_KEY);
        track.end_coord = TestStoredFunction.coordinates_set.get(MESSAGE_KEY+1);

        fee = new TestStoredFunction.Transaction_Profile("", track).transaction_value;
        distance = fee/15;

        Place1 = new MarkerOptions().position(new LatLng(place.get(MESSAGE_KEY)[0], place.get(MESSAGE_KEY)[1]));
        Place2 = new MarkerOptions().position(new LatLng(place.get(MESSAGE_KEY+1)[0], place.get(MESSAGE_KEY+1)[1]));


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        LatLng home1 = new LatLng(place.get(MESSAGE_KEY)[0], place.get(MESSAGE_KEY)[1]);
        LatLng home2 = new LatLng(place.get(MESSAGE_KEY+1)[0], place.get(MESSAGE_KEY+1)[1]);

        Geocoder temp_Geocoder = new Geocoder(this, Locale.getDefault());

        try {
            Address home1_address_add = temp_Geocoder.getFromLocation(home1.latitude, home1.longitude, 1).get(0);
            int home1_add_max_index_int = home1_address_add.getMaxAddressLineIndex();
            if(home1_add_max_index_int > -1)
            {
                String home1_address_str = home1_address_add.getAddressLine(home1_add_max_index_int);
                MarkerOptions home1_marker = new MarkerOptions().position(home1).title(home1_address_str);
                mMap.addMarker(home1_marker);
            }



            Address home2_address_add = temp_Geocoder.getFromLocation(home2.latitude, home2.longitude, 1).get(0);
            int home2_add_max_index_int = home2_address_add.getMaxAddressLineIndex();
            if(home2_add_max_index_int > -1)
            {
                String home2_address_str = home2_address_add.getAddressLine(home2_add_max_index_int);
                MarkerOptions home2_marker = new MarkerOptions().position(home2).title(home2_address_str);
                mMap.addMarker(home2_marker);
            }


            LatLngBounds.Builder boundsBuilder = LatLngBounds.builder().include(home1).include(home2);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 20));
//          mMap.addMarker(Place1).setTitle(String.valueOf(fee) + " VND -"+ String.valueOf(distance) + "m");
//          mMap.addMarker(Place2).setTitle(String.valueOf(fee) + " VND -"+ String.valueOf(distance) + "m");


            line = googleMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .add(home1, home2));
        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}