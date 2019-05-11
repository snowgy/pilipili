package com.example.pilipili;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;

public class EditActivity extends AppCompatActivity {
    String path;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

//        image = findViewById(R.id.editImageView);
//        Intent intent = getIntent();
//        Glide.with(this)
//                .load(intent.getStringExtra("image"))
//                .into(image);
        // image.setImageResource(intent.getIntExtra("image", 0));
    }
}
