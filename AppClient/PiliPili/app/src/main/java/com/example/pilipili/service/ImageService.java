package com.example.pilipili.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pilipili.GridItemActivity;
import com.example.pilipili.MainActivity;
import com.example.pilipili.R;
import com.example.pilipili.model.Image;
import com.example.pilipili.utils.Session;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ImageService extends GeneralService{

    private List<String> images = new ArrayList<>();
    private List<Integer> likes = new ArrayList<>();

    String imgBaseURL = "http://10.20.48.113:8080/api/file/";
    CustomAdapter adapter;
    GridView myGridView;

    public void getAllImages(final Activity activity, GridView gridView) {
        Call<List<String>> req = service.getAllImages();
        final Context mainContext = activity.getBaseContext();
        adapter = new CustomAdapter(activity);
        myGridView = gridView;
        req.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                /**
                 * set image view on the main page
                 */

                if (response.body() != null){
                    System.out.println("---------- not null------------");
                    for(String img : response.body()) {
                        images.add(imgBaseURL + img);
                        System.out.println(imgBaseURL+img);

                    }

//                    LayoutInflater inflater = LayoutInflater.from(mainContext);
//                    View mainView = inflater.inflate(R.layout.activity_main, null);
//                    GridView gridView = mainView.findViewById(R.id.gridView);

                    System.out.println("found gridview");

                    myGridView.setAdapter(adapter);

                    System.out.println("set successful");

                    myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(mainContext, GridItemActivity.class);
                            intent.putExtra("like", likes.get(i));
                            intent.putExtra("image", images.get(i));
                            mainContext.startActivity(intent);
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {

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
            System.out.println("hh");
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
