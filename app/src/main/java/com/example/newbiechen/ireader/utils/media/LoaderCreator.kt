package com.example.newbiechen.ireader.utils.media

import android.content.Context
import androidx.loader.content.CursorLoader

object LoaderCreator {
    val ALL_BOOK_FILE = 1

    fun create(context: Context, id: Int): CursorLoader {
        val loader = if (id == ALL_BOOK_FILE) LocalFileLoader(context) else null
        return loader ?: throw IllegalArgumentException("The id of Loader is invalid!")
    }
}