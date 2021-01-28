package com.example.automatictollwalletjava.vehicle_interaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import static com.example.automatictollwalletjava.MainActivity.mBTDevices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DriverRegisterDialog extends DialogFragment {
    public interface DriverRegisterDialog_inf { public void DriverReg(DialogFragment drv_reg);}
    DriverRegisterDialog.DriverRegisterDialog_inf listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        ArrayList<String> loc_dev_name = new ArrayList<String>();
        for(BluetoothDevice bt_dev : mBTDevices){
            loc_dev_name.add(bt_dev.getName());
        }


        builder.setTitle("Choose a vehicle").setItems(loc_dev_name.toArray(new String[loc_dev_name.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DriverRegisterDialog.this.getDialog().cancel();
            }
        });

        return builder.create();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (DriverRegisterDialog.DriverRegisterDialog_inf) context;
        }catch (ClassCastException e){
            LogError("Exception:..." + e);
        }
    }

    private void LogError(String string)
    {
        Log.d(TAG,this.getClass().getSimpleName() + " : " +  string);
    }


}