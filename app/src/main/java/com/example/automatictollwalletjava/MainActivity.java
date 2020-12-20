package com.example.automatictollwalletjava;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Fragment home_fragment = new HomeFragement();
        final Fragment history_transaction_fragment = new HisotryTransactionFragment();
        final Fragment vehicle_info_fragment = new VehicleFragment();

        Button home_tab = findViewById(R.id.home_tab);
        Button history_tab = findViewById(R.id.history_tab);
        Button vehicle_tab = findViewById(R.id.vehicle_tab);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.FrameLayoutMain,home_fragment)
                .commit();

        home_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction()
                        .replace(R.id.FrameLayoutMain,home_fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        history_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction()
                        .replace(R.id.FrameLayoutMain,history_transaction_fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        vehicle_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction()
                        .replace(R.id.FrameLayoutMain,vehicle_info_fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}