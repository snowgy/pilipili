package com.example.pilipili;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.example.pilipili.model.Image;
import com.example.pilipili.service.GeneralService;
import com.example.pilipili.service.ImageService;
import com.example.pilipili.service.Service;
import com.example.pilipili.utils.Session;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Main Activity */
public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    @BindView(R.id.gridView)
    GridView gridView;
    String baseURL = "http://10.20.35.198:8080/img/";
    String suffix = ".jpeg";
    // CustomAdapter adapter;
    // String[] catComments = {"Cute!", "Lovely", "Aw!", "Mew~", "Little", "QWQ", "Aww", "In love"};
    // String[] catImages = {baseURL+"cat1"+suffix, baseURL+"cat2"+suffix, baseURL+"cat3"+suffix, baseURL+"cat4"+suffix, baseURL+"cat5"+suffix, baseURL+"cat6"+suffix, baseURL+"cat7"+suffix, baseURL+"cat8"+suffix};

    public static final int TAKE_PHOTO_CODE = 1;
    public static final int SELECT_PHOTO_CODE = 2;

    private List<String> images = new ArrayList<>();
    private List<Integer> likes = new ArrayList<>();

    String imgBaseURL = "http://10.20.48.113:8080/api/file/";
    //String[] catComments = {"Cute!", "Lovely"};
    // String[] catImages = {baseURL+"cat1"+suffix, baseURL+"cat2"+suffix};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (!Session.isLogin){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_userhome_black_24dp, "home"))
                .addItem(new BottomNavigationItem(R.drawable.ic_camera_enhance_black_24dp, "camera"))
                .addItem(new BottomNavigationItem(R.drawable.ic_favorite_border_black_24dp, "love"))
                .addItem(new BottomNavigationItem(R.drawable.ic_person_outline_black_24dp, "me"))
                .setFirstSelectedPosition(0)
                .initialise();
        ImageService imageService = new ImageService();
        imageService.getAllImages(this, gridView);
//        GeneralService generalService = new GeneralService();
//        Service service = generalService.getService();
//        Call<List<String>> req = service.getAllImages();
//        adapter = new CustomAdapter(this);
//        req.enqueue(new Callback<List<String>>() {
//            @Override
//            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
//                /**
//                 * set image view on the main page
//                 */
//                if (response.body() != null){
//                    System.out.println("---------- not null------------");
//                    for(String img : response.body()) {
//                        images.add(imgBaseURL + img);
//                        System.out.println(imgBaseURL+img);
//
//                    }
//                    gridView.setAdapter(adapter);
//
//                    System.out.println("set successful");
//
//                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                            Intent intent = new Intent(getApplicationContext(), GridItemActivity.class);
//                            intent.putExtra("like", likes.get(i));
//                            intent.putExtra("image", images.get(i));
//                            startActivity(intent);
//                        }
//                    });
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<String>> call, Throwable t) {
//
//            }
//        });
//        CustomAdapter adapter = new CustomAdapter(this);
//        gridView.setAdapter(adapter);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(getApplicationContext(), GridItemActivity.class);
//                intent.putExtra("name", catComments[i]);
//                intent.putExtra("image", catImages[i]);
//                startActivity(intent);
//            }
//        });
        bottomNavigationBar.setTabSelectedListener(this);
    }

    /**
     * Specify the behavior when a tab is selected
     * @param position the position of the tab
     */
    @Override
    public void onTabSelected(int position) {
        switch (position) {
            case 0:
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                break;
            case 1:
                final Activity context = this;
                if (! cameraPermission()) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_PHOTO_CODE);
                }
                new AlertDialog.Builder(this)
                        .setTitle("Choose your photo")
                        .setItems(new String[]{"Camera", "Album"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                System.out.println("========="+i+"==============");
                                if (i == 0) {
                                    Intent intent = new Intent(context, CameraActivity.class);
                                    intent.putExtra("choice", 0);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(context, CameraActivity.class);
                                    intent.putExtra("choice", 1);
                                    startActivity(intent);
                                }
                            }
                        })
                        .create()
                        .show();
                break;
            case 2:
                //todo implement click behavior of love tab
                break;
            case 3:
                //todo implement click behavior of me tab
                break;
            default:

        }
    }

    /**
     * Specify the behavior when a tab is unselected
     * @param position the position of the tab
     */
    @Override
    public void onTabUnselected(int position) {

    }

    /**
     * Specify the behavior when a tab is reselected
     * @param position the position of the tab
     */
    @Override
    public void onTabReselected(int position) {
        switch (position){
            case 0:
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                break;
            case 1:
                final Activity context = this;
                new AlertDialog.Builder(this)
                        .setTitle("Choose your photo")
                        .setItems(new String[]{"Camera", "Album"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    Intent intent = new Intent(context, CameraActivity.class);
                                    intent.putExtra("choice", 0);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(context, CameraActivity.class);
                                    intent.putExtra("choice", 1);
                                    startActivity(intent);

                                }
                            }
                        })
                        .create()
                        .show();
        }

    }

//    private class CustomAdapter extends BaseAdapter {
//        public Context context;
//
//        public CustomAdapter(Context context){
//            this.context = context;
//        }
//        @Override
//        public int getCount() {
//            return images.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            View view1 = getLayoutInflater().inflate(R.layout.row_data, null);
//            ImageView image = view1.findViewById(R.id.images);
//            Glide.with(context)
//                    .load(images.get(i))
//                    .into(image);
//            return view1;
//        }
//    }


    private boolean cameraPermission() {
        return ContextCompat.checkSelfPermission((Context) MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission((Context) MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean albumPermission() {
        return ContextCompat.checkSelfPermission((Context) MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


}
