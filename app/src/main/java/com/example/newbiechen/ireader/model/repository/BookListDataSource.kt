package com.example.newbiechen.ireader.model.repository

import androidx.paging.PositionalDataSource
import com.example.newbiechen.ireader.model.bean.TagBookBean
import com.example.newbiechen.ireader.model.remote.RemoteRepository

class BookListDataSource(val duration: String, val sort: String, val start: Int, val limit: Int,
                         val tag: String, val gender: String, val remoteRepository: RemoteRepository) : PositionalDataSource<TagBookBean>() {
    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<TagBookBean>) {

    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<TagBookBean>) {

    }

}