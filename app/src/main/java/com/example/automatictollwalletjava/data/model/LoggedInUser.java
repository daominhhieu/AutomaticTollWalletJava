package com.example.automatictollwalletjava.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String username;
    private String displayName;
    private int budget;

    public LoggedInUser(String username, String displayName, int budget) {
        this.username = username;
        this.displayName = displayName;
        this.budget = budget;
    }

    public String getUserId()
    {
        return this.username;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public int getBudget()
    {
        return this.budget;
    }
}