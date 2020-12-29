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

public class LoginFragment extends Fragment {

    public final long LOGIN_TIMEOUT = 10000L;

//    MutableLiveData<String> login_status = new MutableLiveData<String>();
    MutableLiveData<String> input_status = new MutableLiveData<String>();

    EditText PhoneEditText;
    EditText PasswordEditText;

//    TimerTask LoginTask;
//    Timer timer_task;

    MySocketHandler LoginConnection = new MySocketHandler();

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity)getActivity()).bottomNavigationView.setVisibility(View.GONE);
        PhoneEditText = getActivity().findViewById(R.id.login_phone_number);
        PasswordEditText = getActivity().findViewById(R.id.login_password);
        final Button loginButton = getActivity().findViewById(R.id.login);
//        timer_task = new Timer();
//        initiateLoginTask();

        /**The login button**/
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(System.currentTimeMillis() - LoginTask.scheduledExecutionTime() >= LOGIN_TIMEOUT){
//                    LoginTask.cancel();
//                    initiateLoginTask();
//                    timer_task.cancel();
//                    timer_task = new Timer();
//                    timer_task.schedule(LoginTask, 0);
//                }
                try{
                    String phone = PhoneEditText.getText().toString();
                    String password = PasswordEditText.getText().toString();
                    HashMap<String,String> write_info = new HashMap<String,String>();
                    write_info.put("action", "login");
                    write_info.put("phone", phone);
                    write_info.put("password", password);
                    LoginConnection.SetLoginOnGoing();
                    LoginConnection.StartWrite(write_info);
                }catch (Exception e){
                    LogError("Login button error:..."+e.toString());
                }


            }
        });

        LoginConnection.BodyHashMutate.observe(getActivity(), new Observer<HashMap<String, String>>() {
            @Override
            public void onChanged(HashMap<String, String> stringStringHashMap) {
                String result = stringStringHashMap.get("result");
                String action = stringStringHashMap.get("action");
                if (result == null || action == null) {
                    LogError("Null result or action");
                } else if (action.equals("login")) {
                    if(result.equals("good")){
                        ShowToast("Welcome");
                        ((MainActivity) getActivity()).bottomNavigationView.setVisibility(View.VISIBLE);
                        Bundle bundle = new Bundle();
                        for (Map.Entry<String, String> entry : stringStringHashMap.entrySet()) {
                            bundle.putString(entry.getKey(), entry.getValue());
                        }
                        LoginConnection.SetLoginStatusComplete();
                        Navigation.findNavController(view)
                                .navigate(R.id.action_valid_log_in, bundle);
                        return;
                    }
                    LogError("bad result");
                }
                else if(action.equals("virgin")){
                    return;
                }
                else{
                    LogError("no action defined");
                }
                ShowToast("Login failed");
                LoginConnection.SetLoginVirgin();

            }
        });

        /**This part is to judge input validity**/
        input_status.observe( getActivity(), new Observer<String>() {
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

//        /**This part is to judge login validity**/
//        login_status.observe(getActivity(), new Observer<String>() {
//            @Override
//            public void onChanged(String result) {
//                LogError("get login result:..."+ result);
//                if((result.equals("bad"))|| result.isEmpty()){
//                    ShowToast("Login failed");
//                }
//                else{
//                    ShowToast("Welcome");
//                    ((MainActivity) requireActivity()).bottomNavigationView.setVisibility(View.VISIBLE);
//                    Bundle bundle = new Bundle();
//                    for (Map.Entry<String, String> entry : LoginConnection.getBodyHash().entrySet()) {
//                        bundle.putString(entry.getKey(), entry.getValue());
//                    }
//                    Navigation.findNavController(view)
//                            .navigate(R.id.action_valid_log_in, bundle);
//                }
//            }
//        });

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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

//    /**This function run TCP server login conversation**/
//    private void initiateLoginTask(){
//        LoginTask = new TimerTask() {
//            @Override
//            public void run() {
//                while(System.currentTimeMillis() - LoginTask.scheduledExecutionTime() <= LOGIN_TIMEOUT){
//                    LoginConnection = new MySocketHandler(HOST,PORT);
//                    String phone = PhoneEditText.getText().toString();
//                    String password = PasswordEditText.getText().toString();
//                    while(!LoginConnection.GetListeningStatus() || !LoginConnection.GetConnectionStatus());
//                    HashMap<String,String> login_info = new HashMap<String,String>();
//                    login_info.put("action", "login");
//                    login_info.put("phone", phone);
//                    login_info.put("password", password);
//                    LoginConnection.StartWrite(login_info);
//                    while(LoginConnection.getBodyHash().size()==0);
//                    String result = LoginConnection.getBodyHash().get("result");
//                    login_status.postValue(result);
//                    return;
//                }
//                login_status.postValue("login timeout");
//            }
//        };
//    }

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
                && username.length()>8 && username.length()<11;
    }

    /*** A placeholder password validation check ***/
    private boolean isPasswordValid(String password) {
        return StringUtils.isAlphanumeric(password)
                && password.length()>5;
    }
}