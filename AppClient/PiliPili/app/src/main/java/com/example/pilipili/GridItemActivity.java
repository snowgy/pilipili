package com.example.pilipili;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.example.pilipili.service.ImageService;

/** Grid Item Activity */
public class GridItemActivity extends AppCompatActivity {
    TextView like;
    ImageView image;

    int likeNum;
    Long imgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_item);

        like = findViewById(R.id.like);
        image = findViewById(R.id.imageView);
        Intent intent = getIntent();
        likeNum = intent.getIntExtra("likeNum", 0);
        imgId = intent.getLongExtra("imageId", 0);

        like.setText(String.valueOf(likeNum));
        Glide.with(this)
                .load(intent.getStringExtra("image"))
                .into(image);

        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_grid_item_bar);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.mipmap.ic_outline_arrow_back_black_24dp, "home"))
                .addItem(new BottomNavigationItem(R.mipmap.ic_baseline_thumb_up_black_24dp, "like"))
                .setFirstSelectedPosition(0)
                .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                switch (position){
                    case 0:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        break;
                    case 1:
                        like.setText(String.valueOf(++likeNum));
                        ImageService imageService = new ImageService();
                        imageService.updateLikeNum(imgId, likeNum);
                        break;
                }

            }
            @Override
            public void onTabUnselected(int position) {

            }
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
                        like.setText(String.valueOf(++likeNum));
                        ImageService imageService = new ImageService();
                        imageService.updateLikeNum(imgId, likeNum);
                        break;
                }

            }
        });

        // image.setImageResource(intent.getIntExtra("image", 0));
    }
}
