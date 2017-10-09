package com.example.lyy.newjust;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }

        init();

    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        SharedPreferences preferences = getSharedPreferences("theme", MODE_PRIVATE);

        int color = preferences.getInt("color", 0);

        Log.d(TAG, "init: " + color);
        if (color != 0) {
            toolbar.setBackgroundColor(getResources().getColor(color));
        }

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        collapsingToolbarLayout.setTitle("New JUST");

        ImageView head_image_view = (ImageView) findViewById(R.id.head_image_view);
        Glide.with(this).load(R.drawable.head_image).into(head_image_view);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_update:
                Toast.makeText(MainActivity.this, "你点击了更新课表按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_edit:
                Toast.makeText(MainActivity.this, "你点击了编辑课程按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_background:
                Toast.makeText(MainActivity.this, "你点击了更换背景按钮", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.nav_grade:
                Intent intent = new Intent(MainActivity.this, SubjectActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_library:
                Toast.makeText(MainActivity.this, "你点击了馆藏查询按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_setting:
                Toast.makeText(MainActivity.this, "你点击了设置按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_theme:
                showChooseThemeDialog();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showChooseThemeDialog() {
        /* @setView 装入自定义View ==> R.layout.dialog_customize
     * dialog_customize.xml可自定义更复杂的View
     */

        final AlertDialog.Builder customizeDialog =
                new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_customize, null);
        customizeDialog.setTitle("选择主题色");
        customizeDialog.setView(view);
        customizeDialog.setCancelable(true);

        final AlertDialog dialog = customizeDialog.show();
        CircleImageView green = view.findViewById(R.id.green);
        CircleImageView white = view.findViewById(R.id.white);
        CircleImageView purple = view.findViewById(R.id.purple);
        CircleImageView red = view.findViewById(R.id.red);
        CircleImageView blue = view.findViewById(R.id.blue);
        CircleImageView orange = view.findViewById(R.id.orange);
        CircleImageView grey = view.findViewById(R.id.grey);


        green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.material_green_200));
                SharedPreferences.Editor editor = getSharedPreferences("theme", MODE_PRIVATE).edit();
                editor.putInt("color", R.color.material_green_200);
                editor.apply();
                Toast.makeText(getApplicationContext(), "你选择了绿色", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        grey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.material_blue_grey_500));
                SharedPreferences.Editor editor = getSharedPreferences("theme", MODE_PRIVATE).edit();
                editor.putInt("color", R.color.material_blue_grey_500);
                editor.apply();
                Toast.makeText(getApplicationContext(), "你选择了灰色", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.material_white_1000));
                SharedPreferences.Editor editor = getSharedPreferences("theme", MODE_PRIVATE).edit();
                editor.putInt("color", R.color.material_white_1000);
                editor.apply();
                Toast.makeText(getApplicationContext(), "你选择了白色", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.material_deep_orange_400));
                SharedPreferences.Editor editor = getSharedPreferences("theme", MODE_PRIVATE).edit();
                editor.putInt("color", R.color.material_deep_orange_400);
                editor.apply();
                Toast.makeText(getApplicationContext(), "你选择了橘色", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        purple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.material_deep_purple_A200));
                SharedPreferences.Editor editor = getSharedPreferences("theme", MODE_PRIVATE).edit();
                editor.putInt("color", R.color.material_deep_purple_A200);
                Log.d(TAG, "onClick: " + R.color.material_deep_purple_A200);
                editor.apply();
                Toast.makeText(getApplicationContext(), "你选择了紫色", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.material_red_400));
                SharedPreferences.Editor editor = getSharedPreferences("theme", MODE_PRIVATE).edit();
                editor.putInt("color", R.color.material_red_400);
                editor.apply();
                Toast.makeText(getApplicationContext(), "你选择了红色", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.setBackgroundColor(getResources().getColor(R.color.material_blue_400));
                SharedPreferences.Editor editor = getSharedPreferences("theme", MODE_PRIVATE).edit();
                editor.putInt("color", R.color.material_blue_400);
                editor.apply();
                Toast.makeText(getApplicationContext(), "你选择了蓝色", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
