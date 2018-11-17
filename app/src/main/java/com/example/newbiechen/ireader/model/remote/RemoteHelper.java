package com.example.newbiechen.ireader.model.remote;

import com.example.newbiechen.ireader.BuildConfig;
import com.example.newbiechen.ireader.utils.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by newbiechen on 17-4-20.
 */

public class RemoteHelper {
    private static final String TAG = "RemoteHelper";
    private static final int DEFAULT_TIMEOUT = 30;
    private static RemoteHelper sInstance;
    private Retrofit mRetrofit;
    private OkHttpClient mOkHttpClient;
    private RemoteHelper(){

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            // Log信息拦截器
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//这里可以选择拦截级别

            //设置 Debug Log 模式
            builder.addInterceptor(loggingInterceptor);
        }
        mOkHttpClient = builder.build();

        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Constant.API_BASE_URL)
                .build();
    }

    public static RemoteHelper getInstance(){
        if (sInstance == null){
            synchronized (RemoteHelper.class){
                if (sInstance == null){
                    sInstance = new RemoteHelper();
                }
            }
        }
        return sInstance;
    }

    public Retrofit getRetrofit(){
        return mRetrofit;
    }

    public OkHttpClient getOkHttpClient(){
        return mOkHttpClient;
    }
}
