package com.example.automatictollwalletjava;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.automatictollwalletjava.MyUtilities.MySocketHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.automatictollwalletjava.MainActivity.MainActivityBudget;
import static com.example.automatictollwalletjava.MainActivity.MainActivityPhone;
import static com.example.automatictollwalletjava.MainActivity.MainSocketHandler;
import static com.example.automatictollwalletjava.MainActivity.MainActivityVehicle;

public class LoginFragment extends Fragment {

    public final long LOGIN_TIMEOUT = 10000L;
    //TODO: add login timeout timer

    MutableLiveData<String> input_status = new MutableLiveData<String>();

    EditText PhoneEditText;
    EditText PasswordEditText;

    public LoginFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)requireActivity()).bottomNavigationView.setVisibility(View.GONE);
        PhoneEditText = view.findViewById(R.id.login_phone_number);
        PasswordEditText = view.findViewById(R.id.login_password);
        final Button loginButton = view.findViewById(R.id.login);

        /**The login button**/
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String phone = PhoneEditText.getText().toString();
                    String password = PasswordEditText.getText().toString();
                    HashMap<String,String> write_info = new HashMap<String,String>();
                    write_info.put("action", "login");
                    write_info.put("phone", phone);
                    write_info.put("password", password);
                    MainSocketHandler.SetLoginOnGoing();
                    MainSocketHandler.StartWrite(write_info);
                }catch (Exception e){
                    LogError("Login button error:..."+e.toString());
                }


            }
        });

        MainSocketHandler.BodyHashMutate.observe(requireActivity(), new Observer<HashMap<String, String>>() {
            @Override
            public void onChanged(HashMap<String, String> stringStringHashMap) {
                String result = stringStringHashMap.get("result");
                String action = stringStringHashMap.get("action");
                if (result == null || action == null) {
                    LogError("Null result or action");
                } else if (action.equals("login")) {
                    if(result.equals("good")){
                        ShowToast("Welcome");
                        ((MainActivity) requireActivity()).bottomNavigationView.setVisibility(View.VISIBLE);
                        Bundle bundle = new Bundle();
                        for (Map.Entry<String, String> entry : stringStringHashMap.entrySet()) {
                            bundle.putString(entry.getKey(), entry.getValue());
                        }
                        try{
                            MainActivityPhone = stringStringHashMap.get("phone");
                            MainActivityBudget = stringStringHashMap.get("budget");
                            MainActivityVehicle = stringStringHashMap.get("vehicle_name");
                            MainSocketHandler.SetLoginStatusComplete();
                            Navigation.findNavController(view)
                                    .navigate(R.id.action_valid_log_in, bundle);
                        }catch (Exception e){
                            LogError("Exception:..."+e);
                        }
                        return;
                    }
                    LogError("bad result");
                    ShowToast("Login failed");
                    MainSocketHandler.SetLoginVirgin();
                }
            }
        });

        /**This part is to judge input validity**/
        input_status.observe( requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String input_validation) {
                if (input_validation == null) {
                    return;
                }

                if (input_validation.equals("Invalid phone number")) {
                    PhoneEditText.setError(input_validation);
                    return;
                }

                if (input_validation.equals("Invalid password")) {
                    PasswordEditText.setError(input_validation);
                    return;
                }
                loginButton.setEnabled(true);
            }
        });

        /**This part is to check live input format validity**/
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
                loginDataChanged(PhoneEditText.getText().toString(),
                        PasswordEditText.getText().toString());
            }
        };
        PhoneEditText.addTextChangedListener(afterTextChangedListener);
        PasswordEditText.addTextChangedListener(afterTextChangedListener);

    }

    private void LogError(String string)
    {
        Log.d(TAG,this.getClass().getSimpleName() + " : " +  string);
    }

    /**This function is show toast**/
    private void ShowToast(String message) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**This function run TCP server login conversation**/
    private void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            input_status.postValue("Invalid phone number");
        } else if (!isPasswordValid(password)) {
            input_status.postValue("Invalid password");
        } else {
            input_status.postValue("good");
        }
    }

    /*** A placeholder username validation check ***/
    private boolean isUserNameValid(String username) {
        return StringUtils.isNumeric(username)
                && username.length()>9 && username.length()<12;
    }

    /*** A placeholder password validation check ***/
    private boolean isPasswordValid(String password) {
        return password.length()>5;
    }
}