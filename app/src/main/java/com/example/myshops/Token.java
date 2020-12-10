package com.example.myshops;

import com.example.myshops.service.ApiService;
import com.example.myshops.ui.notifications.RetrofitClient;

import okhttp3.HttpUrl;

public class Token {
    public static String currentToken = "";
    public static String urls = "https://fcm.googleapis.com/";

    public static ApiService getFCMClient(){
        return RetrofitClient.getRetrofit(urls).create(ApiService.class);
    }
}