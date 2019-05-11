package com.example.pilipili;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGlassSphereFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSoftLightBlendFilter;

public class CameraActivity extends AppCompatActivity {

    // private final Activity activity;
    private File mImageFile;
    private Uri resultUri;
    private Bitmap globalBitmap;
    private GPUImage gpuImage;

    public static final int TAKE_PHOTO_CODE = 1;
    public static final int SELECT_PHOTO_CODE = 2;

    @BindView(R.id.editImageView)
    ImageView editImageView;

    @BindView(R.id.filter_grey_btn)
    Button greyButton;
    @BindView(R.id.filter_sketch_btn)
    Button sketchButton;
    @BindView(R.id.filter_glass_btn)
    Button glassButton;

//    public CameraActivity(Activity activity) {
//        this.activity = activity;
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        if (getIntent().getIntExtra("choice", 0) == 0)
            tryTakePhoto();
        else{
            selectAlbum();
        }

    }

    private void selectAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(albumIntent, SELECT_PHOTO_CODE);
    }

    private boolean cameraPermission() {
        return ContextCompat.checkSelfPermission((Context) CameraActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission((Context) CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void takePhoto() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            mImageFile = createImageFile();
            if (mImageFile == null)
                return;
            Uri imageUri = FileProvider.getUriForFile(CameraActivity.this, "com.example.pilipili.fileProvider", mImageFile);
            resultUri = imageUri;
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePhotoIntent, TAKE_PHOTO_CODE);
        }
    }

    public void tryTakePhoto() {
        if (cameraPermission()) {
            takePhoto();
        } else {
            ActivityCompat.requestPermissions(CameraActivity.this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKE_PHOTO_CODE);
        }
    }

    /**
     * add filter to the image
     */
    public void filterImage() {
        greyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpuImage.setImage(globalBitmap);
                gpuImage.setFilter(new GPUImageGrayscaleFilter());
                Bitmap fiteredBitmap = gpuImage.getBitmapWithFilterApplied();
                editImageView.setImageBitmap(fiteredBitmap);
            }
        });
        sketchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpuImage.setImage(globalBitmap);
                gpuImage.setFilter(new GPUImageSketchFilter());
                Bitmap fiteredBitmap = gpuImage.getBitmapWithFilterApplied();
                editImageView.setImageBitmap(fiteredBitmap);
            }
        });
        glassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpuImage.setImage(globalBitmap);
                gpuImage.setFilter(new GPUImageGlassSphereFilter());
                Bitmap fiteredBitmap = gpuImage.getBitmapWithFilterApplied();
                editImageView.setImageBitmap(fiteredBitmap);
            }
        });
    }


    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", Environment.getExternalStorageDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (1000 == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode) {
            return;
        }
        if (requestCode == TAKE_PHOTO_CODE) {
            if (resultUri != null) {
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    editImageView.setImageBitmap(bitmap);
                    globalBitmap = bitmap;
                    gpuImage = new GPUImage(CameraActivity.this);
                    filterImage();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else if (requestCode == SELECT_PHOTO_CODE) {
            File imageFile = createImageFile();
            if (imageFile == null)
                return;
            Uri imageUri = data.getData();
            if (imageUri != null) {
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    editImageView.setImageBitmap(bitmap);
                    globalBitmap = bitmap;
                    gpuImage = new GPUImage(CameraActivity.this);
                    filterImage();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
