package com.example.automatictollwalletjava.vehicle_interaction;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.automatictollwalletjava.MyUtilities.MyAdapterHandler;
import com.example.automatictollwalletjava.R;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.example.automatictollwalletjava.MainActivity.mBTDevices_Mutate;

public class VehicleRegisterFragment extends Fragment {

    ArrayList<String> dev_name;
    ArrayList<String> dev_MAC;

    public VehicleRegisterFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vehicle_register_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView Vehicle_lv = (ListView)requireActivity().findViewById(R.id.Vehicle_lv);
        dev_name = new ArrayList<String>();
        dev_MAC = new ArrayList<String>();


        mBTDevices_Mutate.observe(requireActivity(), new Observer<ArrayList<BluetoothDevice>>() {
            @Override
            public void onChanged(ArrayList<BluetoothDevice> bluetoothDevices) {
                try{
                    for(BluetoothDevice items : bluetoothDevices){
                        dev_name.add(items.getName());
                        dev_MAC.add(items.getAddress());
                    }
                    MyAdapterHandler loc_adapter = new MyAdapterHandler(requireActivity(), dev_name, dev_MAC);
                    Vehicle_lv.setAdapter(loc_adapter);
                }catch (Exception e){
                    LogError("Exception:..."+e);
                }

            }
        });

    }

    private void LogError(String string)
    {
        Log.d(TAG,this.getClass().getSimpleName() + " : " +  string);
    }


}