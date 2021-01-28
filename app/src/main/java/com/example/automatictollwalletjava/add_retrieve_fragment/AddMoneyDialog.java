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
import androidx.fragment.app.DialogFragment;

import com.example.automatictollwalletjava.R;

import static android.content.ContentValues.TAG;

public class AddMoneyDialog extends DialogFragment {

    public interface AddMoneyFragment_inf { public void AddAmount(String money);}
    AddMoneyFragment_inf listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View DialogView = inflater.inflate(R.layout.add_money_dialog,null);

        EditText money_in_et = (EditText) DialogView.findViewById(R.id.money_in_et);

        builder.setView(DialogView)
                // Add action buttons
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            listener.AddAmount(money_in_et.getText().toString());
                        }catch (NullPointerException e){
                            LogError("Exception:..."+e);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddMoneyDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            listener = (AddMoneyFragment_inf) context;
        }catch (ClassCastException e){
            LogError("Exception:..." + e);
        }
    }

    private void LogError(String string)
    {
        Log.d(TAG,this.getClass().getSimpleName() + " : " +  string);
    }
}