package com.example.newbiechen.ireader.utils.media

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.loader.content.CursorLoader
import java.io.File

class LocalFileLoader(context: Context) : CursorLoader(context) {

    companion object {
        private val FILE_URI = Uri.parse("content://media/external/file")
        private val SELECTION = MediaStore.Files.FileColumns.DATA + " like ?"
        private val SEARCH_TYPE = "%.txt"
        private val SORT_ORDER = MediaStore.Files.FileColumns.DISPLAY_NAME + " DESC"
        private val FILE_PROJECTION = arrayOf(MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DISPLAY_NAME)
    }

    /**
     * 为 Cursor 设置默认参数
     */
    init {
        uri = FILE_URI
        selection = SELECTION
        selectionArgs = arrayOf(SEARCH_TYPE)
        sortOrder = SORT_ORDER
        projection = FILE_PROJECTION

    }

    fun parseData(cursor: Cursor?, resultCallback: MediaStoreHelper.MediaResultCallback) {
        val files = mutableListOf<File>()
        // 判断是否存在数据
        if (cursor == null) {
            // TODO:当媒体库没有数据的时候，需要做相应的处理
            // 暂时直接返回空数据
            resultCallback.onResultCallback(files)
            return
        }

        // 重复使用Loader时，需要重置cursor的position；
        cursor.moveToPosition(-1)
        while (cursor.moveToNext()) {
            val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
            if (path.isNotBlank()) {
                val file = File(path)
                if (!file.isDirectory && file.exists()) {
                    files.add(file)
                }
            }
        }
        resultCallback.onResultCallback(files)
    }
}