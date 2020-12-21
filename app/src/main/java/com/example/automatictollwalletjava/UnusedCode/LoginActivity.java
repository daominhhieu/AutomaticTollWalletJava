package com.example.automatictollwalletjava.UnusedCode;

import android.Manifest;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.automatictollwalletjava.MainActivity;
import com.example.automatictollwalletjava.R;
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

public class LoginActivity extends AppCompatActivity {

    private static final int PERMISSION_FINE_LOCATION = 99;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
/**This part is for declared action when enter LoginActivity.java**/
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
//                .get(LoginViewModel.class);
//
//
///**This part is for initiate variable linked from layout**/
//        final EditText usernameEditText = findViewById(R.id.username);
//        final EditText passwordEditText = findViewById(R.id.login_password);
//        final Button loginButton = findViewById(R.id.login);
//        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
//        LLatitude_tv = findViewById(R.id.LLatitude_tv);
//
//
///**This part is for declare actions in LoginActivity.java**/
//        locationRequest.setInterval(1000* DEFAULT_INTERVAL_LOC_REQ);
//        locationRequest.setFastestInterval(1000 * FAST_INTERVAL_LOC_REQ);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        /**This part is to display GPS location**/
//        LLatitude_tv.setText(update_GPS_location());
//
//        /**This object initiated to response to changes in text field of "usernameEditText" and "passwordEditText"**/
//        TextWatcher afterTextChangedListener = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // ignore
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // ignore
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
//            }
//        };
//        usernameEditText.addTextChangedListener(afterTextChangedListener);
//        passwordEditText.addTextChangedListener(afterTextChangedListener);
//        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    loginViewModel.login(usernameEditText.getText().toString(),
//                            passwordEditText.getText().toString());
//                }
//                return false;
//            }
//        });
//
//        /**This action is to submit input "usernameEditText" and "passwordEditText" to "loginViewModel" handlers via "loginButton"**/
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);
//                loginViewModel.login(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
//            }
//        });
//
//        /**This part is to get result from "loginViewModel.java" to change to other activity**/
//        loginViewModel.getLoginFormState().observe( this, new Observer<LoginFormState>() {
//            @Override
//            public void onChanged(@Nullable LoginFormState loginFormState) {
//                if (loginFormState == null) {
//                    return;
//                }
//                loginButton.setEnabled(loginFormState.isDataValid());
//                if (loginFormState.getUsernameError() != null) {
//                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
//                }
//                if (loginFormState.getPasswordError() != null) {
//                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
//                }
//            }
//        });
//
//        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
//            @Override
//            public void onChanged(@Nullable LoginResult loginResult) {
//                if (loginResult == null) {
//                    return;
//                }
//                loadingProgressBar.setVisibility(View.GONE);
//                if (loginResult.getError() != null) {
//                    showLoginFailed(loginResult.getError());
//                }
//                if (loginResult.getSuccess() != null) {
//                    updateUiWithUser(loginResult.getSuccess());
//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                }
//                setResult(Activity.RESULT_OK);
//                finish();
//
//                /**Complete and destroy login activity once successful**/
//
//            }
//        });
//
//
//
//        locationCallback =  new LocationCallback(){
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                LLatitude_tv.setText(String.valueOf(locationResult.getLastLocation().getLatitude()));
//            }
//        };
//        setupGPS();
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch(requestCode)
//        {
//            case PERMISSION_FINE_LOCATION:
//                if(grantResults[0] == PackageManager.PERMISSION_DENIED)
//                {
//                    finish();
//                }
//        }
    }

    /**This part is for declare private functions in LoginActivity.java**/

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private String update_GPS_location()
    {
        String loc_str = "HEEY YOOOO1";
        return loc_str;
    }

    private void setupGPS()
    {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LoginActivity.this);
        if(ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
        else
        {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LoginActivity.this);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
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