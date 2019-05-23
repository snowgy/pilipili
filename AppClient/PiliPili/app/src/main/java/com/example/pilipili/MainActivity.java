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
import android.widget.GridView;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.pilipili.service.ImageService;
import com.example.pilipili.utils.Session;
import butterknife.BindView;
import butterknife.ButterKnife;

/** Main Activity */
public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    @BindView(R.id.gridView)
    GridView gridView;

    public static final int SELECT_PHOTO_CODE = 2;


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

    private boolean cameraPermission() {
        return ContextCompat.checkSelfPermission((Context) MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission((Context) MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

}
