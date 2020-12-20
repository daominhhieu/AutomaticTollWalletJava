package com.example.automatictollwalletjava.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView {
    private String displayName;
    private int ID;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName, int ID) {
        this.displayName = displayName;
        this.ID = ID;
    }

    public String getDisplayName() {
        return displayName;
    }
    public int getID()
    {
        return this.ID;
    }
}