package com.example.newbiechen.ireader.model.remote

import com.example.newbiechen.ireader.BuildConfig
import com.example.newbiechen.ireader.utils.Constant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by newbiechen on 17-4-20.
 */

class RemoteHelper private constructor() {
    val retrofit: Retrofit
    private val mOkHttpClient: OkHttpClient

    init {

        val builder = OkHttpClient.Builder()
        builder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            // Log信息拦截器
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY//这里可以选择拦截级别

            //设置 Debug Log 模式
            builder.addInterceptor(loggingInterceptor)
        }
        mOkHttpClient = builder.build()

        retrofit = Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Constant.API_BASE_URL)
                .build()
    }

    companion object {
        private val DEFAULT_TIMEOUT = 30
        private var sInstance: RemoteHelper? = null

        val instance: RemoteHelper
            get() {
                if (sInstance == null) {
                    synchronized(RemoteHelper::class.java) {
                        if (sInstance == null) {
                            sInstance = RemoteHelper()
                        }
                    }
                }
                return sInstance!!
            }
    }

}
