package com.example.pilipili;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.pilipili.service.UploadService;
import com.example.pilipili.utils.BitmapUtils;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageDissolveBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGlassSphereFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLightenBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageOverlayBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;

public class CameraActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {

    private File mImageFile;
    private Uri resultUri;
    protected Bitmap globalBitmap;
    protected Bitmap currentBitmap;

    private int desiredWidth = 0;
    private int desiredHeight = 0;

    protected GPUImage gpuImage;
    private int flag = 0;

    private ImageButton[] styleButtons = new ImageButton[NUM_STYLES];
    private float[] styleValues = new float[NUM_STYLES];

    protected boolean allZero = false;
    private static final boolean NORMALIZE_SLIDERS = true;
    private int lastOtherStyle = 1;

    private final int dimFilter = 0x99000000;
    private int currentStyle = 0;

    private int[] intValues;
    private float[] floatValues;

    protected TensorFlowInferenceInterface inferenceInterface;

    private boolean isCompressed = false;

    private static final String MODEL_FILE = "file:///android_asset/stylize_quantized.pb";

    private static final String INPUT_NODE = "input";
    private static final String STYLE_NODE = "style_num";
    private static final String OUTPUT_NODE = "transformer/expand/conv3/conv/Sigmoid";

    private static final int NUM_STYLES = 26;
    private static final int IGNORE_IMAGE_NUM = 0;
    private final float[] styleVals = new float[NUM_STYLES + IGNORE_IMAGE_NUM];

    public static final int TAKE_PHOTO_CODE = 1;
    public static final int SELECT_PHOTO_CODE = 2;
    public static final int CROP_PHOTO = 3;

    @BindView(R.id.editImageView)
    ImageView editImageView;

    @BindView(R.id.filter_grey_btn)
    Button greyButton;
    @BindView(R.id.filter_sketch_btn)
    Button sketchButton;
    @BindView(R.id.filter_glass_btn)
    Button glassButton;
    @BindView(R.id.filter_lighten_btn)
    Button lightenButton;
    @BindView(R.id.filter_overlay_btn)
    Button overlayButton;
    @BindView(R.id.filter_dissolve_btn)
    Button dissolveButton;


    private boolean isDebug() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        inferenceInterface = new TensorFlowInferenceInterface(getAssets(), MODEL_FILE);
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.edit_nav_bar);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.mipmap.ic_outline_arrow_back_black_24dp, "back"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_restore_black_24dp, "restore"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_save_alt_black_24dp, "download"))
                .addItem(new BottomNavigationItem(R.mipmap.baseline_backup_black_24dp, "upload"))
                .setFirstSelectedPosition(0)
                .initialise();
        bottomNavigationBar.setTabSelectedListener(this);
        LinearLayout styleScroll = findViewById(R.id.style_scroll);
        for (int i = 0; i < NUM_STYLES; ++i) {
            ImageButton styleSlider = new ImageButton(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(10, 0, 10, 0);
            params.height = params.width = 200;
            styleSlider.setLayoutParams(params);
            styleSlider.setImageBitmap(
                    Bitmap.createScaledBitmap(
                            getBitmapFromAsset(CameraActivity.this,
                                    "thumbnails/style" + i + ".jpg"),
                            200, 200, false));
            styleSlider.setColorFilter(dimFilter);
            styleSlider.setOnClickListener(styleButtonListener);
            styleButtons[i] = styleSlider;
            styleValues[i] = 0.0f;
            styleScroll.addView(styleSlider);
        }
        styleButtons[0].setColorFilter(null);
        SeekBar seekBar = findViewById(R.id.style_value_bar);
        seekBar.setOnSeekBarChangeListener(styleValueListener);
        if (getIntent().getIntExtra("choice", 0) == 0)
            takePhoto();
        else {
            selectAlbum();
        }
    }

    private final ImageButton.OnClickListener styleButtonListener = view -> {
        Log.d("EDIT", "styleValues: " + Arrays.toString(styleValues));
        ImageButton button = (ImageButton) view;
        SeekBar seekBar = findViewById(R.id.style_value_bar);
        for (int i = 0; i < NUM_STYLES; i++) {
            ImageButton ib = styleButtons[i];
            if (ib.equals(button)) {
                button.setColorFilter(null);
                seekBar.setProgress((int)(styleValues[i] * 100.0));
                currentStyle = i;
            } else {
                ib.setColorFilter(dimFilter);
            }
        }
    };

    private final SeekBar.OnSeekBarChangeListener styleValueListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            styleValues[currentStyle] = seekBar.getProgress() / 100.0f;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            ProgressDialog myDialog = new ProgressDialog(CameraActivity.this);
            myDialog.setTitle("Processing...");
            myDialog.setMessage("Please wait.");
            myDialog.setIndeterminate(true);
            myDialog.show();
            myDialog.setCancelable(false);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        setStyle(currentStyle, styleValues[currentStyle]);
                        stylizeImage(globalBitmap);
                    } catch (Exception e) {
                        Log.d("PROCESS", "run: " + e.getMessage());
                    }
                    myDialog.dismiss();
                }
            }).start();
        }
    };

    private void setStyle(final int idx, final float value) {
        if (NORMALIZE_SLIDERS) {
            // Slider vals correspond directly to the input tensor vals, and normalization is visually
            // maintained by remanipulating non-selected sliders.
            float otherSum = 0.0f;

            for (int i = 0; i < NUM_STYLES; ++i) {
                if (i != idx) {
                    otherSum += styleValues[i];
                }
            }
            if (otherSum > 0.0) {
                float highestOtherVal = 0;
                final float factor = otherSum > 0.0f ? (1.0f - value) / otherSum : 0.0f;
                for (int i = 0; i < NUM_STYLES; ++i) {
                    if (i == idx)
                        continue;
                    final float newVal = styleValues[i] * factor;
                    styleValues[i] = newVal > 0.01f ? newVal : 0.0f;

                    if (styleValues[i] > highestOtherVal) {
                        lastOtherStyle = i;
                        highestOtherVal = styleValues[i];
                    }
                }
            } else {
                // Everything else is 0, so just pick a suitable slider to push up when the
                // selected one goes down.
                if (lastOtherStyle == idx) {
                    lastOtherStyle = (lastOtherStyle + 1) % NUM_STYLES;
                }
                styleValues[lastOtherStyle] = 1.0f - value;
            }
        }
        float sum = 0.0f;
        for (int i = 0; i < NUM_STYLES; ++i) {
            sum += styleValues[i];
        }
        allZero = sum == 0.0f;
        // Now update the values used for the input tensor. If nothing is set, mix in everything
        // equally. Otherwise everything is normalized to sum to 1.0.
        for (int i = 0; i < NUM_STYLES; ++i) {
            styleVals[i] = allZero ? 1.0f / NUM_STYLES : styleValues[i] / sum;
        }
    }


    private void selectAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(albumIntent, SELECT_PHOTO_CODE);
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


    /**
     * add filter to the image
     */
    public void filterImage() {
        isCompressed = false;
        greyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpuImage.setImage(globalBitmap);
                gpuImage.setFilter(new GPUImageGrayscaleFilter());
                Bitmap fiteredBitmap = gpuImage.getBitmapWithFilterApplied();
                currentBitmap = fiteredBitmap;
                editImageView.setImageBitmap(fiteredBitmap);
            }
        });
        sketchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpuImage.setImage(globalBitmap);
                gpuImage.setFilter(new GPUImageSketchFilter());
                Bitmap fiteredBitmap = gpuImage.getBitmapWithFilterApplied();
                currentBitmap = fiteredBitmap;
                editImageView.setImageBitmap(fiteredBitmap);
            }
        });
        glassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpuImage.setImage(globalBitmap);
                gpuImage.setFilter(new GPUImageGlassSphereFilter());
                Bitmap fiteredBitmap = gpuImage.getBitmapWithFilterApplied();
                currentBitmap = fiteredBitmap;
                editImageView.setImageBitmap(fiteredBitmap);
            }
        });
        lightenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpuImage.setImage(globalBitmap);
                gpuImage.setFilter(new GPUImageLightenBlendFilter());
                Bitmap fiteredBitmap = gpuImage.getBitmapWithFilterApplied();
                currentBitmap = fiteredBitmap;
                editImageView.setImageBitmap(fiteredBitmap);
            }
        });
        overlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpuImage.setImage(globalBitmap);
                gpuImage.setFilter(new GPUImageOverlayBlendFilter());
                Bitmap fiteredBitmap = gpuImage.getBitmapWithFilterApplied();
                currentBitmap = fiteredBitmap;
                editImageView.setImageBitmap(fiteredBitmap);
            }
        });
         dissolveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpuImage.setImage(globalBitmap);
                gpuImage.setFilter(new GPUImageDissolveBlendFilter());
                Bitmap fiteredBitmap = gpuImage.getBitmapWithFilterApplied();
                currentBitmap = fiteredBitmap;
                editImageView.setImageBitmap(fiteredBitmap);
            }
        });

    }

    private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
    }

    /**
     * Style the image
     * @param originBitmap original bitmap
     * @return
     */
    protected Bitmap stylizeImage(Bitmap originBitmap) {
        isCompressed = true;
        Bitmap bitmap = scaleBitmap(originBitmap, desiredWidth, desiredHeight); // desiredSize
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];
            floatValues[i * 3] = ((val >> 16) & 0xFF) / 255.0f;
            floatValues[i * 3 + 1] = ((val >> 8) & 0xFF) / 255.0f;
            floatValues[i * 3 + 2] = (val & 0xFF) / 255.0f;
        }

        // Copy the input data into TensorFlow.
        inferenceInterface.feed(INPUT_NODE, floatValues,
                1, desiredHeight, desiredWidth, 3);
        inferenceInterface.feed(STYLE_NODE, styleVals, NUM_STYLES + IGNORE_IMAGE_NUM);

        // Execute the output node's dependency sub-graph.
        inferenceInterface.run(new String[]{OUTPUT_NODE}, isDebug());

        // Copy the data from TensorFlow back into our array.
        inferenceInterface.fetch(OUTPUT_NODE, floatValues);

        for (int i = 0; i < intValues.length; ++i) {
            intValues[i] =
                    0xFF000000
                            | (((int) (floatValues[i * 3] * 255)) << 16)
                            | (((int) (floatValues[i * 3 + 1] * 255)) << 8)
                            | ((int) (floatValues[i * 3 + 2] * 255));
        }

        bitmap.setPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        editImageView.setImageBitmap(bitmap);
        currentBitmap = bitmap;
        return bitmap;
    }

    private File createImageFile() {
        Locale locale = Locale.CHINA;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", locale).format(new Date());
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
        switch (requestCode) {
            case TAKE_PHOTO_CODE:
                if (resultUri != null) {
                    // cropPhoto(resultUri);
                    Bitmap bitmap;
                    try{
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                        Bitmap cropped = BitmapUtils.cropBitmapToSquare(bitmap);
                        setArgs(cropped);
                        filterImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case SELECT_PHOTO_CODE:
                File imageFile = createImageFile();
                if (imageFile == null)
                    return;
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    Bitmap bitmap;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        Bitmap cropped = BitmapUtils.cropBitmapToSquare(bitmap);
                        setArgs(cropped);
                        filterImage();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
        }
    }

    private void setArgs(Bitmap bitmap) {
        editImageView.setImageBitmap(bitmap);
        globalBitmap = bitmap;
        currentBitmap = globalBitmap;
        desiredWidth = 600;
        desiredHeight = 600;
        gpuImage = new GPUImage(CameraActivity.this);
        intValues = new int[desiredHeight * desiredWidth];
        floatValues = new float[desiredHeight * desiredWidth * 3];
    }

    protected class ImageSlider extends android.support.v7.widget.AppCompatImageView {
        protected float value = 0.0f;
        private boolean hilighted = false;

        private final Paint boxPaint;
        private final Paint linePaint;

        public ImageSlider(final Context context) {
            super(context);
            value = 0.0f;

            boxPaint = new Paint();
            boxPaint.setColor(Color.BLACK);
            boxPaint.setAlpha(128);

            linePaint = new Paint();
            linePaint.setColor(Color.WHITE);
            linePaint.setStrokeWidth(10.0f);
            linePaint.setStyle(Paint.Style.STROKE);
        }

        @Override
        public void onDraw(final Canvas canvas) {
            super.onDraw(canvas);
            final float y = (1.0f - value) * canvas.getHeight();

            // If all sliders are zero, don't bother shading anything.
            if (!allZero) {
                canvas.drawRect(0, 0, canvas.getWidth(), y, boxPaint);
            }

            if (value > 0.0f) {
                canvas.drawLine(0, y, canvas.getWidth(), y, linePaint);
            }

            if (hilighted) {
                canvas.drawRect(0, 0, getWidth(), getHeight(), linePaint);
            }
        }

        @Override
        protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
        }

        public void setValue(final float value) {
            this.value = value;
            postInvalidate();
        }

        public void setHilighted(final boolean highlighted) {
            this.hilighted = highlighted;
            this.postInvalidate();
        }

    }

    public static Bitmap getBitmapFromAsset(final Context context, final String filePath) {
        final AssetManager assetManager = context.getAssets();

        Bitmap bitmap = null;
        try {
            final InputStream inputStream = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (final IOException e) {
           e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * Specify the behavior when a tab is selected
     * @param position the position of the tab
     */
    @Override
    public void onTabSelected(int position) {
        switch (position) {
            case 0:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case 1:
                isCompressed = false;
                currentBitmap = globalBitmap;
                editImageView.setImageBitmap(globalBitmap);
                break;
            case 2:
                boolean success = saveImageToGallery(CameraActivity.this, currentBitmap);
                if (success) {
                    Toast.makeText(getBaseContext(), "successfully save to your album", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "fail to save your photo", Toast.LENGTH_LONG).show();
                }
                break;
            case 3:
                try {
                    File tmp = createImageFile();
                    FileOutputStream fos = new FileOutputStream(tmp);
                    if (isCompressed)
                        currentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    else
                        currentBitmap.compress(Bitmap.CompressFormat.JPEG, 55, fos);
                    UploadService uploadService = new UploadService();
                    uploadService.upload(this, tmp);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        switch (position) {
            case 0:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case 1:
                isCompressed = false;
                currentBitmap = globalBitmap;
                editImageView.setImageBitmap(globalBitmap);
                break;
            case 2:
                boolean success = saveImageToGallery(CameraActivity.this, currentBitmap);
                if (success) {
                    Toast.makeText(getBaseContext(), "successfully save to your album", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "fail to save your photo", Toast.LENGTH_LONG).show();
                }
                break;

            case 3:
                try {
                    File tmp = createImageFile();
                    FileOutputStream fos = new FileOutputStream(tmp);
                    if (!isCompressed)
                        currentBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                    UploadService uploadService = new UploadService();
                    uploadService.upload(this, tmp);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:

        }
    }

    /**
     * save the current image to gallery
     * @param context activity context
     * @param bitmap current bitmap
     * @return
     */
    public boolean saveImageToGallery(Context context, Bitmap bitmap) {
        boolean success = false;
        flag++;
        // save image first
        try {
            FileOutputStream fos = new FileOutputStream(mImageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    mImageFile.getAbsolutePath(), mImageFile.getName()+String.valueOf(flag), null);
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // remind the system album
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        return success;
    }
}
