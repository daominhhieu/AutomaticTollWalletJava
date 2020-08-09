package com.example.automatictollwalletjava;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Polyline line;
    MarkerOptions Place1, Place2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Place1 = new MarkerOptions().position(new LatLng(10.8474307, 106.7703325));
        Place2 = new MarkerOptions().position(new LatLng(10.8476138, 106.7684228));


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
        LatLng home1 = new LatLng(10.8474307, 106.7703325);
        LatLng home2 = new LatLng(10.8476138, 106.7684228);
        mMap.addMarker(new MarkerOptions().position(home2).title("15000 VND"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home2, 4));
        mMap.addMarker(Place1);
        mMap.addMarker(Place2);
        line = googleMap.addPolyline(new PolylineOptions()
            .clickable(true)
            .add(home1, home2));
    }
}