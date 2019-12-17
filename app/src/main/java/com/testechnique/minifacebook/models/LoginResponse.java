package com.testechnique.minifacebook.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {




    @SerializedName("message")
    private boolean message;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public LoginResponse(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public boolean isMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
