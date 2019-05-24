package com.example.pilipili;

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
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.pilipili.model.Image;
import com.example.pilipili.service.UploadService;
import com.example.pilipili.utils.BitmapUtils;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBulgeDistortionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorBurnBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDissolveBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGlassSphereFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLightenBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageOverlayBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageScreenBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;

public class CameraActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {

    private File mImageFile;
    private Uri resultUri;
    protected Bitmap globalBitmap;
    protected Bitmap currentBitmap;

    private int desiredWidth = 0;
    private int desiredHeight = 0;

    protected GPUImage gpuImage;
    private int flag = 0;

    protected boolean allZero = false;
    private static final boolean NORMALIZE_SLIDERS = true;
    private int lastOtherStyle = 1;

    protected ImageGridAdapter imageGridAdapter;
    protected GridView grid;

    private int[] intValues;
    private float[] floatValues;

    protected TensorFlowInferenceInterface inferenceInterface;

    private boolean isCompressed = false;

    private static final String MODEL_FILE = "file:///android_asset/stylize_quantized.pb";

    private static final String INPUT_NODE = "input";
    private static final String STYLE_NODE = "style_num";
    private static final String OUTPUT_NODE = "transformer/expand/conv3/conv/Sigmoid";

    private static final int NUM_STYLES = 8;
    private static final int IGNORE_IMAGE_NUM = 18;
    private final float[] styleVals = new float[NUM_STYLES + IGNORE_IMAGE_NUM];

    public static final int TAKE_PHOTO_CODE = 1;
    public static final int SELECT_PHOTO_CODE = 2;
    public static final int CROP_PHOTO = 3;

    private final View.OnTouchListener gridTouchAdapter =
            new View.OnTouchListener() {
                ImageSlider slider = null;

                @Override
                public boolean onTouch(final View v, final MotionEvent event) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            for (int i = 0; i < NUM_STYLES; ++i) {
                                final ImageSlider child = imageGridAdapter.items[i];
                                final Rect rect = new Rect();
                                child.getHitRect(rect);
                                if (rect.contains((int) event.getX(), (int) event.getY())) {
                                    slider = child;
                                    slider.setHilighted(true);
                                }
                            }
                            break;

                        case MotionEvent.ACTION_MOVE:
                            if (slider != null) {
                                final Rect rect = new Rect();
                                slider.getHitRect(rect);

                                final float newSliderVal =
                                        (float)
                                                Math.min(
                                                        1.0,
                                                        Math.max(
                                                                0.0, 1.0 - (event.getY() - slider.getTop()) / slider.getHeight()));

                                setStyle(slider, newSliderVal);
                                stylizeImage(globalBitmap);
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            if (slider != null) {
                                slider.setHilighted(false);
                                slider = null;
                            }
                            break;

                        default: // fall out
                    }
                    return true;
                }
            };

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
        imageGridAdapter = new ImageGridAdapter();
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.edit_nav_bar);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.mipmap.ic_outline_arrow_back_black_24dp, "back"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_restore_black_24dp, "restore"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_save_alt_black_24dp, "download"))
                .addItem(new BottomNavigationItem(R.mipmap.baseline_backup_black_24dp, "upload"))
                .setFirstSelectedPosition(0)
                .initialise();
        bottomNavigationBar.setTabSelectedListener(this);
        grid = (GridView) findViewById(R.id.grid_layout);
        LinearLayout styleScroll = findViewById(R.id.style_scroll);
        for (int i = 0; i < 25; ++i) {
            ImageSlider styleSlider = new ImageSlider(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(10, 0, 10, 0);
            params.height = params.width = 150;
            styleSlider.setLayoutParams(params);
            styleSlider.setImageBitmap(
                    getBitmapFromAsset(CameraActivity.this, "thumbnails/style" + i + ".jpg"));
            styleScroll.addView(styleSlider);
        }
        styleScroll.setOnTouchListener(gridTouchAdapter);
        grid.setAdapter(imageGridAdapter);
        grid.setOnTouchListener(gridTouchAdapter);
        setStyle(imageGridAdapter.items[0], 1.0f);
        if (getIntent().getIntExtra("choice", 0) == 0)
            takePhoto();
        else {
            selectAlbum();
        }

    }


    protected void setStyle(final ImageSlider slider, final float value) {
        slider.setValue(value);
        if (NORMALIZE_SLIDERS) {
            // Slider vals correspond directly to the input tensor vals, and normalization is visually
            // maintained by remanipulating non-selected sliders.
            float otherSum = 0.0f;

            for (int i = 0; i < NUM_STYLES; ++i) {
                if (imageGridAdapter.items[i] != slider) {
                    otherSum += imageGridAdapter.items[i].value;
                }
            }
            if (otherSum > 0.0) {
                float highestOtherVal = 0;
                final float factor = otherSum > 0.0f ? (1.0f - value) / otherSum : 0.0f;
                for (int i = 0; i < NUM_STYLES; ++i) {
                    final ImageSlider child = imageGridAdapter.items[i];
                    if (child.equals(slider)) {
                        continue;
                    }
                    final float newVal = child.value * factor;
                    child.setValue(newVal > 0.01f ? newVal : 0.0f);

                    if (child.value > highestOtherVal) {
                        lastOtherStyle = i;
                        highestOtherVal = child.value;
                    }
                }
            } else {
                // Everything else is 0, so just pick a suitable slider to push up when the
                // selected one goes down.
                if (imageGridAdapter.items[lastOtherStyle] == slider) {
                    lastOtherStyle = (lastOtherStyle + 1) % NUM_STYLES;
                }
                imageGridAdapter.items[lastOtherStyle].setValue(1.0f - value);
            }
        }
        final boolean lastAllZero = allZero;
        float sum = 0.0f;
        for (int i = 0; i < NUM_STYLES; ++i) {
            sum += imageGridAdapter.items[i].value;
        }
        allZero = sum == 0.0f;
        // Now update the values used for the input tensor. If nothing is set, mix in everything
        // equally. Otherwise everything is normalized to sum to 1.0.
        for (int i = 0; i < NUM_STYLES; ++i) {
            styleVals[i] = allZero ? 1.0f / NUM_STYLES : imageGridAdapter.items[i].value / sum;
            if (lastAllZero != allZero) {
                imageGridAdapter.items[i].postInvalidate();
            }
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
        // Bitmap bitmap = scaleBitmap(originBitmap, 256, 256); // desiredSize
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

    protected class ImageGridAdapter extends BaseAdapter {
        protected final ImageSlider[] items = new ImageSlider[NUM_STYLES];
        final ArrayList<Button> buttons = new ArrayList<>();
        public ImageGridAdapter() {
            for (int i = 0; i < NUM_STYLES; ++i) {
                if (items[i] == null) {
                    final ImageSlider slider = new ImageSlider(CameraActivity.this);
                    final Bitmap bm =
                            getBitmapFromAsset(CameraActivity.this, "thumbnails/style" + i + ".jpg");
                    slider.setImageBitmap(bm);
                    items[i] = slider;
                }
            }
        }
        @Override
        public int getCount() {
            return buttons.size() + NUM_STYLES;
        }

        @Override
        public Object getItem(final int position) {
            if (position < buttons.size()) {
                return buttons.get(position);
            } else {
                return items[position - buttons.size()];
            }
        }

        @Override
        public long getItemId(final int position) {
            return getItem(position).hashCode();
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            if (convertView != null) {
                return convertView;
            }
            return (View) getItem(position);
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
