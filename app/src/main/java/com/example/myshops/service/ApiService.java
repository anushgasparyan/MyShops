package com.example.myshops.service;

import com.example.myshops.ui.notifications.Response;
import com.example.myshops.ui.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers({ "Content-Type:application/json",
            "Authorization:key=AAAAUzybeDA:APA91bH0Z5pYO2TiaUvCPXO45RtkBVTl72MixrIB3elTmoYQmKUYF9KjRxyBimF6r9q7gPRK8EYrHBF8weYyDjUjgPC50lOityTE0HD5-dhnUwQxHqZ3tmZZGSJyxFbMcCOEkil2Bil6"})

    @POST("fcm/send")
    Call<Response> sender(@Body Sender body);

}
