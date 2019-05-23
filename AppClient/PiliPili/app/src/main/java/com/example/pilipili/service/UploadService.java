package com.example.pilipili.service;

import android.app.Activity;
import android.widget.Toast;

import com.example.pilipili.utils.Session;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Provide upload service
 */
public class UploadService extends GeneralService {
    /**
     * Upload the file to server
     * @param activity the activity that calls upload
     * @param file file to upload
     */
    public void upload(final Activity activity, File file) {
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
        RequestBody userName = RequestBody.create(MediaType.parse("multipart/form-data"), Session.userName);

        retrofit2.Call<okhttp3.ResponseBody> req = service.uploadImg(body, userName);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    Toast.makeText(activity.getBaseContext(), "upload success", Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }
}
