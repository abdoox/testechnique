package com.testechnique.minifacebook.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
 private static Retrofit retrofit = null;

    public static  Retrofit getClient () {

        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://testechnique.000webhostapp.com/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }
        return retrofit;

    }}
