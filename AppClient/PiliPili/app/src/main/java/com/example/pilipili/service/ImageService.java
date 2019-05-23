package com.example.pilipili.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pilipili.GridItemActivity;
import com.example.pilipili.R;
import com.example.pilipili.model.Image;
import com.example.pilipili.utils.Session;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageService extends GeneralService{

    private List<Long> imageIds = new ArrayList<>();
    private List<String> images = new ArrayList<>();
    private List<Integer> likes = new ArrayList<>();

    String imgBaseURL = "http://10.20.48.113:8080/api/file/";
    CustomAdapter adapter;
    GridView myGridView;

    /**
     * Get all images from server
     * One interesting observation: you must pass the gridView
     * from the mainActivity. If you inflate the view in the
     * callback, it would fail.
     * @param activity
     * @param gridView
     */
    public void getAllImages(final Activity activity, GridView gridView) {
        Call<List<Image>> req = service.getAllImages();
        final Context mainContext = activity.getBaseContext();
        adapter = new CustomAdapter(activity);
        myGridView = gridView;
        req.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                /**
                 * set image view on the main page
                 */

                if (response.body() != null){
                    for(Image img : response.body()) {
                        imageIds.add(img.getId());
                        images.add(imgBaseURL + img.getPath());
                        likes.add(img.getLikeNum());
                    }

                    myGridView.setAdapter(adapter);

                    myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(mainContext, GridItemActivity.class);
                            intent.putExtra("imageId", imageIds.get(i));
                            intent.putExtra("likeNum", likes.get(i));
                            intent.putExtra("image", images.get(i));
                            mainContext.startActivity(intent);
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {

            }
        });
    }

    public void updateLikeNum(long imgId, int likeNum){
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", imgId);
        params.put("likes", likeNum);
        Gson gson = new Gson();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(params));
        Call<ResponseBody> req = service.updateLikeNum(requestBody);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }


    private class CustomAdapter extends BaseAdapter {
        public Context context;

        public CustomAdapter(Context context){
            this.context = context;
        }
        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int i) {
            return images.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view1 = inflater.inflate(R.layout.row_data, null);
            ImageView image = view1.findViewById(R.id.images);
            Glide.with(context)
                    .load(images.get(i))
                    .into(image);
            return view1;
        }
    }

}
