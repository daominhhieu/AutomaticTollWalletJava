package com.example.automatictollwalletjava.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayName;
    private int ID;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName, int ID) {
        this.displayName = displayName;
        this.ID = ID;
    }

    String getDisplayName() {
        return displayName;
    }
    int getID()
    {
        return this.ID;
    }
}