package com.example.automatictollwalletjava.data;

import com.example.automatictollwalletjava.data.model.LoggedInUser;
import com.example.automatictollwalletjava.TestStoredFunction;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
/*
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
*/

            Result res = new TestStoredFunction().VerifiedUser(username, password);

            return res;
        } catch (Exception e) {
            return new Result.Error("Error logging in");
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}