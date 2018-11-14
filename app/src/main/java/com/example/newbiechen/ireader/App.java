package com.example.newbiechen.ireader;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.example.newbiechen.ireader.service.DownloadService;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by newbiechen on 17-4-15.
 */

public class App extends Application {
    private static Context sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        startService(new Intent(getContext(), DownloadService.class));

        // 初始化内存分析工具
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

//        BlockCanary.install(this, new AppBlockCanaryContext()).start();

        Stetho.initializeWithDefaults(this);
    }

    public static Context getContext(){
        return sInstance;
    }
}