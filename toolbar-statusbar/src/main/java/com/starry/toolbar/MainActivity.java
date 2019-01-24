package com.starry.toolbar;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) return;

        toolbar.setNavigationIcon(R.drawable.toolbar_navigation_icon); //返回键图标
        toolbar.inflateMenu(R.menu.menu_refresh); //右上角menu
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_item_refresh:
                        Toast.makeText(getApplicationContext(), "menu刷新", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

    protected void initToolbar2() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.toolbar_navigation_icon);
            setSupportActionBar(toolbar);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);//自定义标题居中需要关闭
            actionBar.setDisplayHomeAsUpEnabled(true);//显示返回键
        }

        //透明状态栏，不需要请注释掉
        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().setStatusBarColor(Color.TRANSPARENT); //状态栏跟随标题栏颜色
        }

        //沉浸式状态栏
        if (Build.VERSION.SDK_INT >= 16) {//SYSTEM_UI_FLAG_LAYOUT_STABLE required 16
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //返回键
                finish();
                break;

            case R.id.menu_item_refresh: //右上角的menu
                Toast.makeText(getApplicationContext(), "menu刷新", Toast.LENGTH_LONG).show();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
