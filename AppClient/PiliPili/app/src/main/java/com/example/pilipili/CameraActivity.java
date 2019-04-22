package com.example.pilipili;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class CameraActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final Activity activity;

    public CameraActivity(Activity activity) {
        this.activity = activity;
    }

    private boolean cameraPermission() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void takePhoto() {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePhotoIntent, 1001);
        }
    }

    public void tryTakePhoto() {
        if (cameraPermission()) {
            takePhoto();
        } else {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, 1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (1000 == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            }
        }
    }
}
