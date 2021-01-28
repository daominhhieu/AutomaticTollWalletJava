package com.example.automatictollwalletjava;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.automatictollwalletjava.add_retrieve_fragment.AddMoneyDialog;
import com.example.automatictollwalletjava.add_retrieve_fragment.RetrieveMoneyDialog;
import com.example.automatictollwalletjava.vehicle_interaction.DriverRegisterDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;
import static com.example.automatictollwalletjava.MainActivity.MainActivityBudget;
import static com.example.automatictollwalletjava.MainActivity.MainActivityPhone;
import static com.example.automatictollwalletjava.MainActivity.MainActivityVehicle;
import static com.example.automatictollwalletjava.MainActivity.MainActivityVehicleMass;
import static com.example.automatictollwalletjava.MainActivity.MainSocketHandler;
import static com.example.automatictollwalletjava.MainActivity.mBTDevices_Mutate;
import static com.example.automatictollwalletjava.MainActivity.mBluetoothAdapter;
import static com.example.automatictollwalletjava.MainActivity.mBTDevices;

public class homeFragment extends Fragment {

    String action_home = "";
    Timer FetchBudgetTimer;

    private HashMap<String, String> message = new HashMap<String, String>();

    private Thread BudgetUpdaterThread;

    public homeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button add_money_btn = view.findViewById(R.id.add_money_btn);
        Button retrieve_money_btn = view.findViewById(R.id.retrieve_money_btn);
        Button register_driver_btn = view.findViewById(R.id.register_driver_btn);
        Button register_vehicle_btn = view.findViewById(R.id.register_vehicle_btn);
        Button debug_btn = (Button) view.findViewById(R.id.debug_btn);
        Button log_out_btn = (Button) view.findViewById(R.id.log_out_btn);
        TextView info = view.findViewById(R.id.user_info_tv);

        String info_layout =
                "Phone Number:  " + getArguments().getString("phone") + "\n"
               +"Vehicle name:  " + getArguments().getString("vehicle_name") + "\n"
               +"Budget      :  " + getArguments().getString("budget");

        info.setText(info_layout);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        add_money_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action_home ="add_money_btn";
                AddMoneyDialog addMoneyDialog = new AddMoneyDialog();
                addMoneyDialog.show(requireActivity().getSupportFragmentManager(), "AddMoneyDialog");
            }
        });

        retrieve_money_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action_home ="retrieve_money_btn";
                RetrieveMoneyDialog retrieveMoneyDialog = new RetrieveMoneyDialog();
                retrieveMoneyDialog.show(requireActivity().getSupportFragmentManager(), "RetrieveMoneyDialog");
            }
        });

        register_driver_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action_home ="register_driver_btn";
                enableBT();
            }
        });

        register_vehicle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action_home ="register_vehicle_btn";
                enableBT();
                Navigation.findNavController(view)
                        .navigate(R.id.action_vehicle_register);
            }
        });

        debug_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action_home ="debug_btn";
                Navigation.findNavController(view)
                        .navigate(R.id.action_debug);
            }
        });

        log_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    action_home ="log_out_btn";
                    message.put("phone", MainActivityPhone);
                    message.put("action", "logout");
                    MainSocketHandler.StartWrite(message);
                }catch (Exception e){
                    LogError("Log out button error:..." + e.toString());
                }

            }
        });

        MainSocketHandler.BodyHashMutate.observe(requireActivity(), new Observer<HashMap<String, String>>() {
            @Override
            public void onChanged(HashMap<String, String> stringStringHashMap) {
                String result = stringStringHashMap.get("result");
                String action = stringStringHashMap.get("action");
                if(action == null || result == null){
                    LogError("Null result or action");
                    return;
                }
                if (action.equals("logout")) {
                    if (result.equals("good")) {
                        try{
                            MainSocketHandler.SetLoginVirgin();
                            Navigation.findNavController(view)
                                    .navigate(R.id.action_logout);
                        }catch (Exception e){
                            LogError("Exception:..."+e);
                        }
                        return;
                    }
                    LogError("Bad result");
                }
                else if(action.equals("getuserinfo")){
                    if (result.equals("good")) {
                        try{
                            MainActivityBudget = stringStringHashMap.get("budget");
                            MainActivityVehicle = stringStringHashMap.get("vehicle_name");
                            MainActivityVehicleMass = Integer.parseInt(stringStringHashMap.get("vehicle_mass"));
                            String info_layout =
                                    "Phone Number:  " + MainActivityPhone + "\n"
                                            +"Vehicle name:  " + MainActivityVehicle + "\n"
                                            +"Budget      :  " + MainActivityBudget;

                            info.setText(info_layout);
                        }catch (Exception e){
                            LogError("Exception:..."+e);
                        }
                        return;
                    }
                }
            }
        });


        BudgetUpdaterThread = new Thread(new BudgetUpdater());
        BudgetUpdaterThread.start();

    }

    private void LogError(String string)
    {
        Log.d(TAG,this.getClass().getSimpleName() + " : " +  string);
    }

    private class BudgetUpdater implements Runnable {
        public void run(){
            FetchBudgetTimer = new Timer();
            try {
                message.put("phone", MainActivityPhone);
                message.put("action", "getuserinfo");
                MainSocketHandler.StartWrite(message);
            }catch (Exception e){
                LogError("Exception:..."+e);
            }
            FetchBudgetTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    BudgetUpdaterThread = new Thread(new BudgetUpdater());
                    BudgetUpdaterThread.start();
                }
            }, 5000L);
        }
    }

    @Override
    public void onDestroyView() {
        LogError("DESTROY VIEW OF HOME FRAGMENT");
        super.onDestroyView();
        FetchBudgetTimer.cancel();
        FetchBudgetTimer.purge();
    }

    public void enableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
            return;
        }
        try {
            if(!mBluetoothAdapter.isEnabled()){
                Log.d(TAG, "enableDisableBT: enabling BT.");
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBTIntent);

                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                requireActivity().registerReceiver(mBroadcastReceiver1, BTIntent);
            }
            if(mBluetoothAdapter.isEnabled()){
                Log.d(TAG, "enableDisableBT: disabling BT.");
                mBluetoothAdapter.disable();

                IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                requireActivity().registerReceiver(mBroadcastReceiver1, BTIntent);
            }
        }catch (Exception e){
            LogError("Exception:..."+e);
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        LogError( "onReceive: STATE OFF");
                        enableBT();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        LogError( "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        LogError( "mBroadcastReceiver1: STATE ON");
                        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                        mBTDevices.clear();
                        if (pairedDevices.size() > 0) {
                            // There are paired devices. Get the name and address of each paired device.
                            for (BluetoothDevice loc_device : pairedDevices) {
                                mBTDevices.add(loc_device);
                                LogError( loc_device.getName());
                            }
                        }
                        mBTDevices_Mutate.postValue(mBTDevices);
                        //discoverBT();
                        if(action_home == "register_driver_btn"){
                            DriverRegisterDialog driverRegisterDialog = new DriverRegisterDialog();
                            driverRegisterDialog.show(requireActivity().getSupportFragmentManager(), "DriverRegisterDialog");
                        }

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        LogError( "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        LogError( "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        LogError( "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        LogError( "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        LogError( "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        LogError( "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };




    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            LogError( "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                LogError( "onReceive: " + device.getName() + ": " + device.getAddress());
            }
        }
    };

    public void discoverBT() {
        LogError( "btnDiscover: Looking for unpaired devices.");
        if(!mBluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            requireActivity().registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = requireActivity().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += requireActivity().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            LogError( "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
}