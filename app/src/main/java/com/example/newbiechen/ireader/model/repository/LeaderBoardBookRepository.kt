package com.example.newbiechen.ireader.model.repository

import androidx.lifecycle.MutableLiveData
import com.example.newbiechen.ireader.model.bean.BillBookBean
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.viewmodel.BaseViewModelData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LeaderBoardBookRepository private constructor(val remoteRepository: RemoteRepository) {

    fun fetchLeaderBoardBook(id: String): BaseViewModelData<List<BillBookBean>> {
        val isRequestInProgress = MutableLiveData<Boolean>()
        val toastMsg = MutableLiveData<String>()
        val data = MutableLiveData<List<BillBookBean>>()
        isRequestInProgress.postValue(true)
        remoteRepository.getBillBooks(id).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { response ->
                            isRequestInProgress.postValue(false)
                            data.postValue(response)
                        },
                        { error ->
                            isRequestInProgress.postValue(false)
                            toastMsg.postValue(error.toString())
                        }

                )
        return BaseViewModelData(isRequestInProgress, toastMsg, data)
    }

    companion object {
        // For singleton instantiation
        @Volatile private var instance: LeaderBoardBookRepository? = null

        fun getInstance(remoteRepository: RemoteRepository) = instance ?: synchronized(this) {
            instance ?: LeaderBoardBookRepository(remoteRepository).also { instance = it }
        }
    }

}