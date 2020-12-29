package com.example.automatictollwalletjava;

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

import com.example.automatictollwalletjava.MyUtilities.MySocketHandler;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class homeFragment extends Fragment {

    String action_mutate = "";

    MySocketHandler homeSocketHandler = new MySocketHandler();
    HashMap<String, String> message = new HashMap<String, String>();


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
        final Button add_money_btn = (Button) getActivity().findViewById(R.id.add_money_btn);
        Button retrieve_money_btn = (Button) getActivity().findViewById(R.id.retrieve_money_btn);
        Button register_driver_btn = (Button) getActivity().findViewById(R.id.register_driver_btn);
        Button register_vehicle_btn = (Button) getActivity().findViewById(R.id.register_vehicle_btn);
        Button debug_btn = (Button) getActivity().findViewById(R.id.debug_btn);
        Button log_out_btn = (Button) getActivity().findViewById(R.id.log_out_btn);

        add_money_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action_mutate="add_money_btn";
                Navigation.findNavController(view)
                        .navigate(R.id.action_add_money);
            }
        });

        retrieve_money_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action_mutate="retrieve_money_btn";
                Navigation.findNavController(view)
                        .navigate(R.id.action_homeFragment_to_retreiveMoneyFragment);
            }
        });

        register_driver_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action_mutate="register_driver_btn";
                Navigation.findNavController(view)
                        .navigate(R.id.action_driver_register);
            }
        });

        register_vehicle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action_mutate="register_vehicle_btn";
                Navigation.findNavController(view)
                        .navigate(R.id.action_vehicle_register);
            }
        });

        debug_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action_mutate="debug_btn";
                Navigation.findNavController(view)
                        .navigate(R.id.action_debug);
            }
        });

        log_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    action_mutate="log_out_btn";
                    message.put("phone", getArguments().getString("phone"));
                    message.put("action", "logout");
                    homeSocketHandler.StartWrite(message);
                }catch (Exception e){
                    LogError("Log out button error:..." + e.toString());
                }

            }
        });

        homeSocketHandler.BodyHashMutate.observe(getActivity(), new Observer<HashMap<String, String>>() {
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
                        homeSocketHandler.SetLoginVirgin();
                        Navigation.findNavController(view)
                                .navigate(R.id.action_logout);
                        return;
                    }
                    LogError("Bad result");
                }
                else if(action.equals("getuserinfo")){

                }

                LogError("No action defined");

            }
        });

    }

    private void LogError(String string)
    {
        Log.d(TAG,this.getClass().getSimpleName() + " : " +  string);
    }


}