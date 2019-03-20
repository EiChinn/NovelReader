package com.example.newbiechen.ireader.model.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.newbiechen.ireader.model.bean.SortBookBean
import com.example.newbiechen.ireader.model.remote.RemoteRepository

/**
 * A simple data source factory which also provides observe the last created data source.
 * This allows us to channel its network request status etc back to the UI
 */
class BookCategoryDataSourceFactory(val gender: String, val type: String, val major: String, val minor: String, val remoteRepository: RemoteRepository) : DataSource.Factory<Int, SortBookBean>() {
    val sourceLiveData = MutableLiveData<BookCategoryDataSource>()
    override fun create(): DataSource<Int, SortBookBean> {
        val source = BookCategoryDataSource(gender, type, major, minor, remoteRepository)
        sourceLiveData.postValue(source)
        return source
    }

}