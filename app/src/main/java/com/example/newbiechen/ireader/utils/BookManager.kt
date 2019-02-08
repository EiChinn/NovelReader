package com.example.newbiechen.ireader.utils

import java.io.File

object BookManager {
    /**
     * 创建或获取存储文件
     * @param folderName
     * @param fileName
     * @return
     */
    @JvmStatic
    fun getBookFile(folderName: String, fileName: String): File {
        return FileUtils.getFile(Constant.BOOK_CACHE_PATH + folderName
                + File.separator + fileName + FileUtils.SUFFIX_NB)
    }

    @JvmStatic
    fun getBookSize(folderName: String): Long {
        return FileUtils.getDirSize(FileUtils
                .getFolder(Constant.BOOK_CACHE_PATH + folderName))
    }

    /**
     * 根据文件名判断是否被缓存过 (因为可能数据库显示被缓存过，但是文件中却没有的情况，所以需要根据文件判断是否被缓存
     * 过)
     * @param folderName : bookId
     * @param fileName: chapterName
     * @return
     */
    @JvmStatic
    fun isChapterCached(folderName: String, fileName: String): Boolean {
        val file = File(Constant.BOOK_CACHE_PATH + folderName
                + File.separator + fileName + FileUtils.SUFFIX_NB)
        return file.exists()
    }
}