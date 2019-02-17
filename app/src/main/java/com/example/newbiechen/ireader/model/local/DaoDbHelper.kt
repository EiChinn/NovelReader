package com.example.newbiechen.ireader.model.local

import android.database.sqlite.SQLiteDatabase

import com.example.newbiechen.ireader.App
import com.example.newbiechen.ireader.model.gen.DaoMaster
import com.example.newbiechen.ireader.model.gen.DaoSession

/**
 * Created by newbiechen on 17-4-26.
 */

class DaoDbHelper private constructor() {
    val database: SQLiteDatabase
    private val mDaoMaster: DaoMaster
    val session: DaoSession

    val newSession: DaoSession
        get() = mDaoMaster.newSession()

    init {
        //封装数据库的创建、更新、删除
        // TODO: 2018/11/17 先将数据库升级改成删表重建，等换源重建后再改回来
        val openHelper = DaoMaster.DevOpenHelper(App.getInstance(), DB_NAME, null)
        //获取数据库
        database = openHelper.writableDatabase
        //封装数据库中表的创建、更新、删除
        mDaoMaster = DaoMaster(database)  //合起来就是对数据库的操作
        //对表操作的对象。
        session = mDaoMaster.newSession() //可以认为是对数据的操作
    }

    companion object {
        private val DB_NAME = "IReader_DB"

        @Volatile
        private var sInstance: DaoDbHelper? = null


        val instance: DaoDbHelper?
            get() {
                if (sInstance == null) {
                    synchronized(DaoDbHelper::class.java) {
                        if (sInstance == null) {
                            sInstance = DaoDbHelper()
                        }
                    }
                }
                return sInstance
            }
    }
}
