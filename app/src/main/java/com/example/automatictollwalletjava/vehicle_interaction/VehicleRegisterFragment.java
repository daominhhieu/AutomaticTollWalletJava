package com.example.automatictollwalletjava.vehicle_interaction;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.automatictollwalletjava.MyUtilities.MyAdapterHandler;
import com.example.automatictollwalletjava.R;

import java.util.ArrayList;
import java.util.Set;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VehicleRegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VehicleRegisterFragment extends Fragment {

    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    ListView paired_device_lv;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VehicleRegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VehicleRegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VehicleRegisterFragment newInstance(String param1, String param2) {
        VehicleRegisterFragment fragment = new VehicleRegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vehicle_register, container, false);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode)
        {
            case REQUEST_ENABLE_BT:
                if(grantResults[0] == PackageManager.PERMISSION_DENIED)
                {
                    LogError("BLE permission denied");
                }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Button enableBLE = getActivity().findViewById(R.id.enableBLE);
        paired_device_lv = getActivity().findViewById(R.id.paired_vehicle_lv);


        enableBLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetUpBLE();
            }
        });

    }

    private void LogError(String string)
    {
        Log.d(TAG,this.getClass().getSimpleName() + " : " +  string);
    }

    private void SetUpBLE()
    {
        if(bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else {
                bluetoothAdapter.disable();
            }
            IntentFilter loc_filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            getActivity().registerReceiver(receiver_state_changed, loc_filter);
        }
        else
        {
            LogError("BLE not available");
        }
    }

    private void GetPairedBLE()
    {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList<String> deviceNameList = new ArrayList<String>();
        ArrayList<String> deviceMACList =new ArrayList<String>();
        if (pairedDevices.size() > 0) {
            LogError("number of paired devices: " + String.valueOf(pairedDevices.size()));
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String tmp_device_name = device.getName();
                String tmp_device_MAC = device.getAddress();

                LogError("device name: " + tmp_device_name + "  MAC address: "+ tmp_device_MAC);

                deviceNameList.add(tmp_device_name);
                deviceMACList.add(tmp_device_MAC);// MAC address
            }
            MyAdapterHandler ble_list_adapter = new MyAdapterHandler(getActivity(), deviceNameList, deviceMACList);
            paired_device_lv.setAdapter(ble_list_adapter);

        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver_device_found = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };

    // Create a BroadcastReceiver for ACTION_STATE_CHANGED.
    private final BroadcastReceiver receiver_state_changed = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);
                switch (state)
                {
                    case BluetoothAdapter.STATE_OFF:
                        LogError("BLE STATE_OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        LogError("BLE STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        LogError("BLE STATE_ON");
                        GetPairedBLE();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        LogError("BLE STATE_TURNING_ON");
                        break;
                    case BluetoothAdapter.STATE_DISCONNECTING:
                        LogError("BLE STATE_DISCONNECTING");
                        break;
                    case BluetoothAdapter.STATE_DISCONNECTED:
                        LogError("BLE STATE_DISCONNECTED");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        LogError("BLE STATE_CONNECTED");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        LogError("BLE STATE_CONNECTING");
                        break;
                    default:
                        LogError("BLE Error or other State");
                        break;
                }

            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver_state_changed);
    }

}