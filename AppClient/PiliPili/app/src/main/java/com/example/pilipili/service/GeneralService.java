package com.example.pilipili.service;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeneralService {
    public static String baseURL = "http://10.20.48.113:8080";
    // public static String baseURL = "http://10.20.35.198:8080";
    public static HttpLoggingInterceptor interceptor;
    public static OkHttpClient client;
    public static Service service;
    public GeneralService(){
        interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        service = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build()
                .create(Service.class);
    }

    public Service getService(){
        return service;
    }
}
