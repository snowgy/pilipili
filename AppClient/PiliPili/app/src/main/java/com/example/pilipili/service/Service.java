package com.example.pilipili.service;

import com.example.pilipili.model.Image;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Served as retrofit interface
 */
public interface Service {

    @Multipart
    @POST("/uploadImg")
    Call<ResponseBody> uploadImg(@Part MultipartBody.Part image, @Part("username") RequestBody userName);

    @POST("/getAllImages")
    Call<List<Image>> getAllImages();

    @FormUrlEncoded
    @POST("/getUserImages")
    Call<List<Image>> getUserImages(@Field("userName") String userName);

    @FormUrlEncoded
    @POST("/getLovedImages")
    Call<List<Image>> getLovedImages(@Field("userName") String userName);

    @POST("/updateLikeNum")
    Call<ResponseBody> updateLikeNum(@Body RequestBody body);

    @FormUrlEncoded
    @POST("/deleteImage")
    Call<ResponseBody> deleteImage(@Field("imgId") long imgId);

}
