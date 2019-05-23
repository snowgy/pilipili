package com.example.pilipili.service;

import com.example.pilipili.model.Image;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Served as retrofit interface
 */
public interface Service {
    @Multipart
    @POST("/uploadImg")
    Call<ResponseBody> uploadImg(@Part MultipartBody.Part image, @Part("username") RequestBody userName);

    @POST("/getAllImages")
    Call<List<String>> getAllImages();
}
