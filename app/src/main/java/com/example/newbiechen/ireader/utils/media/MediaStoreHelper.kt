package com.example.newbiechen.ireader.utils.media

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import java.io.File
import java.lang.ref.WeakReference

/**
 * Created by newbiechen on 2018/1/14.
 * 获取媒体库的数据。
 */
object MediaStoreHelper {

    @JvmStatic
    fun getAllBookFile(activity: FragmentActivity, resultCallback: MediaResultCallback) {
        // 将文件的获取处理交给 LoaderManager。
        activity.supportLoaderManager.initLoader(LoaderCreator.ALL_BOOK_FILE, null, MediaLoaderCallbacks(activity, resultCallback))

    }

    interface MediaResultCallback {
        fun onResultCallback(files: List<@JvmSuppressWildcards File>)
    }

    /**
     * Loader 回调处理
     */
    class MediaLoaderCallbacks(context: Context, private val mResultCallback: MediaResultCallback) : LoaderManager.LoaderCallbacks<Cursor> {
        protected val mContext = WeakReference<Context>(context)

        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> = LoaderCreator.create(mContext.get()!!, id)

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
            (loader as LocalFileLoader).parseData(data, mResultCallback)
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {
        }

    }

}