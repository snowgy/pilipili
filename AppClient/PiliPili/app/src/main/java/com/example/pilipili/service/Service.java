package com.example.pilipili.service;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Served as retrofit interface
 */
interface Service {
    @Multipart
    @POST("/uploadImg")
    Call<ResponseBody> uploadImg(@Part MultipartBody.Part image);
}
