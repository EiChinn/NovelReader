package com.example.newbiechen.ireader

import android.app.Application
import android.content.Intent
import android.text.TextUtils
import com.example.newbiechen.ireader.service.DownloadService
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import com.tencent.bugly.crashreport.CrashReport
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

/**
 * Created by newbiechen on 17-4-15.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        startService(Intent(this, DownloadService::class.java))

        //多进程（如：android:process=":push"）会导致Application的onCreate多次调用，有N个进程就调用N次，所以这里要按进程进行初始化
        // 获取当前包名
        val processName = getProcessName(android.os.Process.myPid())
        if (processName == null || processName == packageName) {
            initBugly()

            initRealm()
            if (BuildConfig.DEBUG) {
                //			initLeakCanary();
                //			initBlockCanary();
            }
        }

        initLeakCanary()

    }

    private fun initLeakCanary() {
        // 初始化内存分析工具
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

        //        BlockCanary.install(this, new AppBlockCanaryContext()).start();

    }

    private fun initRealm() {
        Stetho.initializeWithDefaults(this)

    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                reader?.close()
            } catch (exception: IOException) {
                exception.printStackTrace()
            }

        }
        return null
    }

    private fun initBugly() {
        val context = applicationContext
        // 获取当前包名
        //		String packageName = context.getPackageName();
        // 获取当前进程名
        //		String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        val strategy = CrashReport.UserStrategy(context)
        strategy.isUploadProcess = true
        //区分开发设备
        CrashReport.setIsDevelopmentDevice(context, BuildConfig.DEBUG)
        // 初始化Bugly
        CrashReport.initCrashReport(context, "60a9303ae5", BuildConfig.DEBUG, strategy)
        // 如果通过“AndroidManifest.xml”来配置APP信息，初始化方法如下
        // CrashReport.initCrashReport(context, strategy);
        // CrashReport.initCrashReport(getApplicationContext(), "6c0163115b", false);
    }

    companion object {
        private lateinit var instance: App
        fun getInstance(): App = instance

    }
}