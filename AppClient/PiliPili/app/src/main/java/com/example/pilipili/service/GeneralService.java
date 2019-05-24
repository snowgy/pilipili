package com.example.pilipili.service;

import com.example.pilipili.utils.Data;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeneralService {
    public String baseURL = Data.baseUrl;
    public HttpLoggingInterceptor interceptor;
    public OkHttpClient client;
    public Service service;
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
}
