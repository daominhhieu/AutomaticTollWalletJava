package com.example.automatictollwalletjava.data;

import android.util.Log;

import com.example.automatictollwalletjava.data.model.LoggedInUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private HashMap<String, String> userinformation_local_test = new HashMap<String, String>();

    public LoginDataSource()
    {
        userinformation_local_test.put("daominhhieu3@gmail.com", "Hieu2312.");
        userinformation_local_test.put("toiluonlahieu@gmail.com", "Hieu2312.");
    }
    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
/*
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
*/

            Result res = localServerInterface_test(username,password);

            return res;
        } catch (Exception e) {
            return new Result.Error("Error logging in");
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    private Result localServerInterface_test(String username, String password)
    {
        for(String item : userinformation_local_test.keySet())
        {
             if(username.equals(item))
             {
                 if(userinformation_local_test.get(item).equals(password))
                 {
                     return new Result.Success<>(new LoggedInUser(username,password));
                 }
             }
        }
        return new Result.Error("Login Fail");
    }
}