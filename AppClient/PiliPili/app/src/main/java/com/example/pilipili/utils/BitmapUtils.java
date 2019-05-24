package com.example.pilipili.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public final class BitmapUtils {

    private BitmapUtils(){

    }
    /**
     * compress image to a file
     * @param bmp bitmap to be used
     * @param file output file
     */
    public static void compressImageToFile(Bitmap bmp, File file) {
        int options = 50;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        try (FileOutputStream fos =  new FileOutputStream(file)){
            fos.write(baos.toByteArray());
            fos.flush();
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
        return Bitmap.createBitmap(src, cropW, cropH, newWidth, newHeight);
    }

    static Bitmap getBorderedBitmap(Bitmap image, int borderColor, int borderSize) {

        // Creating a canvas with an empty bitmap, this is the bitmap that gonna store the final canvas changes
        Bitmap finalImage = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(finalImage);

        // Make a smaller copy of the image to draw on top of original
        Bitmap imageCopy = Bitmap.createScaledBitmap(image, image.getWidth() - borderSize, image.getHeight() - borderSize, true);

        // Let's draw the bigger image using a white paint brush
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(borderColor, PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(image, 0, 0, paint);

        int width = image.getWidth();
        int height = image.getHeight();
        float centerX = (width - imageCopy.getWidth()) * 0.5f;
        float centerY = (height - imageCopy.getHeight()) * 0.5f;
        // Now let's draw the original image on top of the white image, passing a null paint because we want to keep it original
        canvas.drawBitmap(imageCopy, centerX, centerY, null);

        // Returning the image with the final results
        return finalImage;
    }

}
