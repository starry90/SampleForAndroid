package com.starry.smartflexboxlayout;

import android.app.Application;
import android.content.Context;

/**
 * @author Starry
 * @since 18-1-2.
 */

public class MyApplication extends Application {

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }
}
