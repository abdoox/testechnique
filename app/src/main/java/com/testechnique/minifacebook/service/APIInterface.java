package com.testechnique.minifacebook.service;



import com.testechnique.minifacebook.models.LoginResponse;
import com.testechnique.minifacebook.models.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface APIInterface {

    @FormUrlEncoded
    @POST("signup.php")
    Call<SignupResponse> signup(
            @Field("name") String name,
            @Field("login") String login,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> login(
            @Field("login") String login,
            @Field("password") String password
    );

}

