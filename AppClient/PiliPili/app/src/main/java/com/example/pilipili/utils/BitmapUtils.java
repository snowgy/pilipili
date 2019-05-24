package com.example.pilipili.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class BitmapUtils {
    /**
     * compress image to a file
     * @param bmp bitmap to be used
     * @param file output file
     */
    public static void compressImageToFile(Bitmap bmp, File file) {
        int options = 50;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap cropBitmapToSquare(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - (height - width) : height;
        int cropW = (width - height)/2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width)/2;
        cropH = (cropH < 0)? 0: cropH;
        Bitmap cropImg = Bitmap.createBitmap(src, cropW, cropH, newWidth, newHeight);
        return cropImg;
    }

}
