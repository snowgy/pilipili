package com.example.pilipili;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.example.pilipili.utils.Session;

import butterknife.BindView;
import butterknife.ButterKnife;

/** Main Activity */
public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    @BindView(R.id.gridView)
    GridView gridView;
    String baseURL = "http://10.20.35.198:8080/img/";
    String suffix = ".jpeg";
    String[] catComments = {"Cute!", "Lovely", "Aw!", "Mew~", "Little", "QWQ", "Aww", "In love", "God", "Cool"};
    String[] catImages = {baseURL+"cat1"+suffix, baseURL+"cat2"+suffix, baseURL+"cat3"+suffix, baseURL+"cat4"+suffix, baseURL+"cat5"+suffix, baseURL+"cat6"+suffix, baseURL+"cat7"+suffix, baseURL+"cat8"+suffix, baseURL+"cat9"+suffix, baseURL+"cat10"+suffix};
    //String[] catComments = {"Cute!", "Lovely"};
    // String[] catImages = {baseURL+"cat1"+suffix, baseURL+"cat2"+suffix};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (!Session.isLogin){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_userhome_black_24dp, "home"))
                .addItem(new BottomNavigationItem(R.drawable.ic_camera_enhance_black_24dp, "camera"))
                .addItem(new BottomNavigationItem(R.drawable.ic_favorite_border_black_24dp, "love"))
                .addItem(new BottomNavigationItem(R.drawable.ic_person_outline_black_24dp, "me"))
                .setFirstSelectedPosition(0)
                .initialise();
        CustomAdapter adapter = new CustomAdapter(this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), GridItemActivity.class);
                intent.putExtra("name", catComments[i]);
                intent.putExtra("image", catImages[i]);
                startActivity(intent);
            }
        });
        bottomNavigationBar.setTabSelectedListener(this);
    }

    /**
     * Specify the behavior when a tab is selected
     * @param position the position of the tab
     */
    @Override
    public void onTabSelected(int position) {
        switch (position) {
            case 0:
                //todo implement click behavior of home tab
                break;
            case 1:
                final Activity context = this;
                new AlertDialog.Builder(this)
                        .setTitle("Choose your photo")
                        .setItems(new String[]{"Camera", "Album"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                System.out.println("========="+i+"==============");
                                if (i == 0) {
                                    Intent intent = new Intent(context, CameraActivity.class);
                                    intent.putExtra("choice", 0);
                                    startActivity(intent);
                                } else {
                                    Intent intent = new Intent(context, CameraActivity.class);
                                    intent.putExtra("choice", 1);
                                    startActivity(intent);
                                }
                            }
                        })
                        .create()
                        .show();
                break;
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
        if (1 == position) {
//            new CameraActivity(this).tryTakePhoto();
            final Activity context = this;
            new AlertDialog.Builder(this)
                    .setTitle("Choose your photo")
                    .setItems(new String[]{"Camera", "Album"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == 0) {
//                                new CameraActivity().tryTakePhoto();
                                Intent intent = new Intent(context, CameraActivity.class);
                                intent.putExtra("choice", 0);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(context, CameraActivity.class);
                                intent.putExtra("choice", 1);
                                startActivity(intent);
                            }
                        }
                    })
                    .create()
                    .show();
        }
    }

    private class CustomAdapter extends BaseAdapter {
        public Context context;

        public CustomAdapter(Context context){
            this.context = context;
        }
        @Override
        public int getCount() {
            return catImages.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = getLayoutInflater().inflate(R.layout.row_data, null);
            TextView name = view1.findViewById(R.id.texts);
            ImageView image = view1.findViewById(R.id.images);
            name.setText(catComments[i]);
            Glide.with(context)
                    .load(catImages[i])
                    .into(image);
            //image.setImageResource(catImages[i]);
            return view1;
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
