package com.example.employeemood;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("customer-josh-reason-today")
    Call<JsonElement> getMoodData(@Header("Authorization") String authKey, @Query("user_profile") int value);
}
