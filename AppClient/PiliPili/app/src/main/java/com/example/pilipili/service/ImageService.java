package com.example.pilipili.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.pilipili.MainActivity;
import com.example.pilipili.R;
import com.example.pilipili.model.Image;
import com.example.pilipili.utils.Data;
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

    public List<Image> allImages = new ArrayList<>();
    public List<Image> userImages = new ArrayList<>();
    public List<Image> favoImages = new ArrayList<>();

    String imgBaseURL = Data.imgBaseUrl;
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
        adapter = new CustomAdapter(activity, allImages);
        myGridView = gridView;
        req.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                /**
                 * set image view on the main page
                 */
                allImages.clear();

                if (response.body() != null){
                    for(Image img : response.body()) {
                        allImages.add(img);
                    }

                    myGridView.setAdapter(adapter);

                    myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(mainContext, GridItemActivity.class);
                            Image image = allImages.get(i);
                            intent.putExtra("imageId", image.getId());
                            intent.putExtra("likeNum", image.getLikeNum());
                            intent.putExtra("image", imgBaseURL + image.getPath());
                            intent.putExtra("userName", image.getUserName());
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

    public void updateLikeNum(String userName, long imgId, int likeNum){
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("userName", userName);
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

    public void getUserImages(final Activity activity, GridView gridView){
        Call<List<Image>> req = service.getUserImages(Session.getUserName());
        final Context mainContext = activity.getBaseContext();
        adapter = new CustomAdapter(activity, userImages);
        myGridView = gridView;

        req.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                userImages.clear();
                if (response.body() != null){
                    for(Image img : response.body()) {
                        userImages.add(img);
                    }
                    if (userImages.size() == 0) {
                        new AlertDialog.Builder(activity)
                                .setTitle("You have no photo now")
                                .setItems(new String[]{"Go to upload"}, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            Intent intent = new Intent(activity.getBaseContext(), MainActivity.class);
                                            activity.startActivity(intent);
                                        }
                                    }
                                })
                                .create()
                                .show();

                    }
                    myGridView.setAdapter(adapter);

                    myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(mainContext, GridItemActivity.class);
                            Image image = userImages.get(i);
                            intent.putExtra("imageId", image.getId());
                            intent.putExtra("likeNum", image.getLikeNum());
                            intent.putExtra("image", imgBaseURL + image.getPath());
                            intent.putExtra("userName", image.getUserName());
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

    public void getLovedImages(final Activity activity, GridView gridView){
        Call<List<Image>> req = service.getLovedImages(Session.getUserName());
        final Context mainContext = activity.getBaseContext();
        adapter = new CustomAdapter(activity, favoImages);
        myGridView = gridView;

        req.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                favoImages.clear();
                if (response.body() != null){
                    for(Image img : response.body()) {
                        favoImages.add(img);
                    }

                    if (favoImages.size() == 0) {
                        new AlertDialog.Builder(activity)
                                .setTitle("No Loving Photo now")
                                .setItems(new String[]{"Explore more"}, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            Intent intent = new Intent(activity.getBaseContext(), MainActivity.class);
                                            activity.startActivity(intent);
                                        }
                                    }
                                })
                                .create()
                                .show();

                    }

                    myGridView.setAdapter(adapter);

                    myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(mainContext, GridItemActivity.class);
                            Image image = favoImages.get(i);
                            intent.putExtra("imageId", image.getId());
                            intent.putExtra("likeNum", image.getLikeNum());
                            intent.putExtra("image", imgBaseURL + image.getPath());
                            intent.putExtra("userName", image.getUserName());
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

    public void deleteImage(final Context context, long imgId){
        Call<ResponseBody> req = service.deleteImage(imgId);
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(context, "delete successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    private class CustomAdapter extends BaseAdapter {
        public Context context;
        public List<Image> ownImages;

        public CustomAdapter(Context context, List<Image> images){
            this.context = context;
            this.ownImages = images;
        }
        @Override
        public int getCount() {
            return ownImages.size();
        }

        @Override
        public Object getItem(int i) {
            return ownImages.get(i);
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
            String path = ownImages.get(i).getPath();
            Glide.with(context)
                    .load(imgBaseURL + path)
                    .into(image);
            return view1;
        }
    }

}
