package com.example.newbiechen.ireader.model.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.example.newbiechen.ireader.model.bean.SortBookBean
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BookCategoryDataSource(val gender: String, val type: String, val major: String, val minor: String, val remoteRepository: RemoteRepository) : PositionalDataSource<SortBookBean>() {

    val isRequestInProgress = MutableLiveData<Boolean>()
    val toastMsg = MutableLiveData<String>()

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<SortBookBean>) {
        isRequestInProgress.postValue(true)
        val result = remoteRepository.getSortBookPage(gender, type, major, minor, params.startPosition, params.loadSize)
        result.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe (
                {response ->
                    callback.onResult(response.books)
                    isRequestInProgress.postValue(false)
                },
                { error ->
                    isRequestInProgress.postValue(false)
                    toastMsg.postValue(error.toString())
                })
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<SortBookBean>) {
        isRequestInProgress.postValue(true)
        val result = remoteRepository.getSortBookPage(gender, type, major, minor, params.requestedStartPosition, params.requestedLoadSize)
        result.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe (
            {response ->
                if (response.ok) {
                    callback.onResult(response.books, params.requestedStartPosition, response.total)
                }
                isRequestInProgress.postValue(false)
            },
            { error ->
                isRequestInProgress.postValue(false)
                toastMsg.postValue(error.toString())
            })
    }

}