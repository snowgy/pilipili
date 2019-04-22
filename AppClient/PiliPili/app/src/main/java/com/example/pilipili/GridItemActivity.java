package com.example.pilipili;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;

/** Grid Item Activity */
public class GridItemActivity extends AppCompatActivity {
    TextView name;
    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_item);

        name = findViewById(R.id.gridData);
        image = findViewById(R.id.imageView);
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_grid_item_bar);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.mipmap.ic_outline_arrow_forward_black_24dp, "home"))
                .setFirstSelectedPosition(0)
                .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
            @Override
            public void onTabUnselected(int position) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

            }
            @Override
            public void onTabReselected(int position) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        Intent intent = getIntent();
        name.setText(intent.getStringExtra("name"));
        Glide.with(this)
                .load(intent.getStringExtra("image"))
                .into(image);
        // image.setImageResource(intent.getIntExtra("image", 0));
    }
}
