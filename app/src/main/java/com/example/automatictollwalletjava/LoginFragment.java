package com.example.automatictollwalletjava;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.automatictollwalletjava.ui.login.LoggedInUserView;
import com.example.automatictollwalletjava.ui.login.LoginFormState;
import com.example.automatictollwalletjava.ui.login.LoginResult;
import com.example.automatictollwalletjava.ui.login.LoginViewModel;
import com.example.automatictollwalletjava.ui.login.LoginViewModelFactory;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    public static final int PERMISSION_FINE_LOCATION = 2;
    public static final int DEFAULT_INTERVAL_LOC_REQ = 30;
    public static final int FAST_INTERVAL_LOC_REQ = 5;
    private LoginViewModel loginViewModel;
    /**Google's API for location services ==> IMPORTANT!**/
    FusedLocationProviderClient fusedLocationProviderClient;
    /**Configuration for location services : "fusedLocationProviderClient"**/
    LocationRequest locationRequest = new LocationRequest();
    /**Get location**/
    LocationCallback locationCallback;
    TextView LLatitude_tv;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                getActivity().finish();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((MainActivity)getActivity()).bottomNavigationView.setVisibility(View.GONE);

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);


/**This part is for initiate variable linked from layout**/
        final EditText usernameEditText = getActivity().findViewById(R.id.login_phone_number);
        final EditText passwordEditText = getActivity().findViewById(R.id.login_password);
        final Button loginButton = getActivity().findViewById(R.id.login);
        LLatitude_tv = getActivity().findViewById(R.id.LLatitude_tv);


/**This part is for declare actions in LoginActivity.java**/
        locationRequest.setInterval(1000* DEFAULT_INTERVAL_LOC_REQ);
        locationRequest.setFastestInterval(1000 * FAST_INTERVAL_LOC_REQ);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        /**This part is to display GPS location**/
        LLatitude_tv.setText("NULL");

        /**This object initiated to response to changes in text field of "usernameEditText" and "passwordEditText"**/
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        /**This action is to submit input "usernameEditText" and "passwordEditText" to "loginViewModel" handlers via "loginButton"**/
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        /**This part is to get result from "loginViewModel.java" to change to other activity**/
        loginViewModel.getLoginFormState().observe( getActivity(), new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(getActivity(), new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                    ((MainActivity)getActivity()).bottomNavigationView.setVisibility(View.VISIBLE);
                    Navigation.findNavController(view)
                            .navigate(R.id.action_valid_log_in);

                }

                /**Complete and destroy login activity once successful**/

            }
        });



        locationCallback =  new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LLatitude_tv.setText(String.valueOf(locationResult.getLastLocation().getLatitude()));
            }
        };
        setupGPS();

    }
    /**This part is for declare private functions in LoginActivity.java**/

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        Toast.makeText(getActivity().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getActivity().getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void setupGPS()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
        else
        {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null)
                    {
                        LLatitude_tv.setText(String.valueOf(location.getLatitude()));
                    }

                }
            });
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }



}