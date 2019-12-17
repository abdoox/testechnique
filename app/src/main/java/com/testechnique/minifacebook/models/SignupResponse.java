package com.testechnique.minifacebook.models;

import com.google.gson.annotations.SerializedName;

public class SignupResponse {


    @SerializedName("message")

    private boolean message;


    public boolean isMessage() {
        return message;
    }

}
