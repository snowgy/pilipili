package com.example.pilipili;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Trace;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGlassSphereFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSoftLightBlendFilter;

public class CameraActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {

    // private final Activity activity;
    private File mImageFile;
    private Uri resultUri;
    private Bitmap globalBitmap;
    private Bitmap currentBitmap;

    private int desiredWidth = 0;
    private int desiredHeight = 0;

    private GPUImage gpuImage;
    private int frameNum = 0;
    private boolean DEBUG = true;

    private boolean allZero = false;
    private static final boolean NORMALIZE_SLIDERS = true;
    private int lastOtherStyle = 1;

    private ImageGridAdapter imageGridAdapter;
    private GridView grid;

    private int[] intValues;
    private float[] floatValues;

    private TensorFlowInferenceInterface inferenceInterface;

    private static final String MODEL_FILE = "file:///android_asset/stylize_quantized.pb";

    private static final String INPUT_NODE = "input";
    private static final String STYLE_NODE = "style_num";
    private static final String OUTPUT_NODE = "transformer/expand/conv3/conv/Sigmoid";

    private static final int NUM_STYLES = 18;
    private static final int IGNORE_IMAGE_NUM = 8;
    private final float[] styleVals = new float[NUM_STYLES + IGNORE_IMAGE_NUM];

    public static final int TAKE_PHOTO_CODE = 1;
    public static final int SELECT_PHOTO_CODE = 2;

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
                            // stylizeImage(globalBitmap);
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
                                // stylizeImage(globalBitmap);
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

    private boolean isDebug() {
        return DEBUG;
    }

    //    public CameraActivity(Activity activity) {
//        this.activity = activity;
//    }
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
        grid.setAdapter(imageGridAdapter);
        grid.setOnTouchListener(gridTouchAdapter);
        setStyle(imageGridAdapter.items[0], 1.0f);
        if (getIntent().getIntExtra("choice", 0) == 0)
            tryTakePhoto();
        else {
            selectAlbum();
        }

    }


    private void setStyle(final ImageSlider slider, final float value) {
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
                    if (child == slider) {
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
            ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKE_PHOTO_CODE);
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
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
//        if (!origin.isRecycled()) {
//            origin.recycle();
//        }
        return newBM;
    }

    private Bitmap stylizeImage(Bitmap originBitmap) {
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
                    setArgs(bitmap);
                    // croppedBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
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
                    setArgs(bitmap);
                    // croppedBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
                    filterImage();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void setArgs(Bitmap bitmap) {
        editImageView.setImageBitmap(bitmap);
        globalBitmap = bitmap;
        desiredWidth = 240;
        desiredHeight = 320;
        gpuImage = new GPUImage(CameraActivity.this);
        intValues = new int[desiredHeight * desiredWidth];
        floatValues = new float[desiredHeight * desiredWidth * 3];
    }

    private class ImageSlider extends android.support.v7.widget.AppCompatImageView {
        private float value = 0.0f;
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

    private class ImageGridAdapter extends BaseAdapter {
        final ImageSlider[] items = new ImageSlider[NUM_STYLES];
        final ArrayList<Button> buttons = new ArrayList<>();

        {
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

    private static void rgbTransformation(Bitmap bitmap) {

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
                editImageView.setImageBitmap(globalBitmap);
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
        switch (position) {
            case 0:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case 1:
                editImageView.setImageBitmap(globalBitmap);
            case 2:
                //todo implement click behavior of love tab
                break;
            case 3:
                //todo implement click behavior of me tab
                break;
            default:

        }
    }

}
