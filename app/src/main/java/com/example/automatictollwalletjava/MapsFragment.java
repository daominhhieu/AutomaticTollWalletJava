package com.example.automatictollwalletjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.Locale;

import static com.example.automatictollwalletjava.MainActivity.fee_Map;
import static com.example.automatictollwalletjava.MainActivity.pos1_Map;
import static com.example.automatictollwalletjava.MainActivity.pos2_Map;

public class MapsFragment extends Fragment {

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Geocoder temp_Geocoder = new Geocoder(requireActivity(), Locale.getDefault());

            try {
                Double dist = SphericalUtil.computeDistanceBetween(pos1_Map, pos2_Map);
                Address home1_address_add = temp_Geocoder.getFromLocation(pos1_Map.latitude, pos1_Map.longitude, 1).get(0);
                int home1_add_max_index_int = home1_address_add.getMaxAddressLineIndex();
                if (home1_add_max_index_int > -1) {
                    String home1_address_str = home1_address_add.getAddressLine(home1_add_max_index_int);
                    MarkerOptions home1_marker = new MarkerOptions().position(pos1_Map).title(home1_address_str + "\n" + fee_Map + " VND -" + dist + "m");
                    googleMap.addMarker(home1_marker);
                }
                Address home2_address_add = temp_Geocoder.getFromLocation(pos2_Map.latitude, pos2_Map.longitude, 1).get(0);
                int home2_add_max_index_int = home2_address_add.getMaxAddressLineIndex();
                if (home2_add_max_index_int > -1) {
                    String home2_address_str = home2_address_add.getAddressLine(home2_add_max_index_int);
                    MarkerOptions home2_marker = new MarkerOptions().position(pos2_Map).title(home2_address_str + "\n" + fee_Map + " VND -" + dist + "m");
                    googleMap.addMarker(home2_marker);
                }
                LatLngBounds.Builder boundsBuilder = LatLngBounds.builder().include(pos1_Map).include(pos2_Map);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 20));

                Polyline line = googleMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                        .add(pos1_Map, pos2_Map));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}