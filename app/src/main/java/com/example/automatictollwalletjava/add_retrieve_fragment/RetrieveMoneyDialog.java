package com.example.automatictollwalletjava.add_retrieve_fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.automatictollwalletjava.R;

import static android.content.ContentValues.TAG;

public class RetrieveMoneyDialog extends DialogFragment {
    public interface RetrieveMoneyDialog_inf { public void RetrieveAmount(String money);}
    RetrieveMoneyDialog_inf listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View DialogView = inflater.inflate(R.layout.retrieve_money_dialog,null);

        EditText money_out_et = (EditText) DialogView.findViewById(R.id.money_out_et);

        builder.setView(DialogView)
                // Add action buttons
                .setPositiveButton("Retrieve", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            listener.RetrieveAmount(money_out_et.getText().toString());
                        }catch (NullPointerException e){
                            LogError("Exception:..."+e);
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RetrieveMoneyDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (RetrieveMoneyDialog.RetrieveMoneyDialog_inf) context;
        }catch (ClassCastException e){
            LogError("Exception:..." + e);
        }
    }

    private void LogError(String string)
    {
        Log.d(TAG,this.getClass().getSimpleName() + " : " +  string);
    }
}