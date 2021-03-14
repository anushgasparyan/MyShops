package com.example.myshops;

import com.example.myshops.service.ApiService;
import com.example.myshops.ui.notifications.RetrofitClient;

public class Token {
    public static String currentToken = "eIqpNBDfRqCdDziecupqTW:APA91bFFf-ARS887UsrX4D7pMRDPwT4qKPYtGm0o6DuNKAAf_RRw7penwfh_oLV6F_suN6-82WtKROdv0_rvrtuc_B9g45ise5PlzVHnaQ7NHnpRL8OXvnnFEGnrwCgbA6z787CJdBSu";
    public static String urls = "https://fcm.googleapis.com/";

    public static ApiService getFCMClient(){
        return RetrofitClient.getRetrofit(urls).create(ApiService.class);
    }
}