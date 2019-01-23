package com.starry.process;

import android.app.Application;

/**
 * @author Starry
 * @since 2017/6/3.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new AppForeBackStatusCallback());
    }
}
