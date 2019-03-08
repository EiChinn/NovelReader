package com.example.newbiechen.ireader.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BookCategoryDetailRepository private constructor(private val remoteRepository: RemoteRepository) {
    val isRequestInProgress = MutableLiveData<Boolean>()
    val toastMsg = MutableLiveData<String>()
    fun fetchBookCategoryMin(gender: String, majorCategory: String): LiveData<List<String>> {
        isRequestInProgress.postValue(true)
        val result = MutableLiveData<List<String>>()
        val disposable = remoteRepository.bookSubSortPackage.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map{ if (gender == "gender") it.male else it.female}
                .map{ it.first { gender -> gender.major == majorCategory }.mins  }
                .subscribe (
                        { response ->
                            result.postValue(response)
                            isRequestInProgress.postValue(false)
                        },
                        {error ->
                            toastMsg.postValue(error.toString())
                        })
        return result

    }
    companion object {
        // For Singleton Instantiation
        @Volatile private var instance: BookCategoryDetailRepository? = null
        fun getInstance(remoteRepository: RemoteRepository): BookCategoryDetailRepository {
            return instance ?: synchronized(this) {
                instance ?: BookCategoryDetailRepository(remoteRepository).also { instance = it }
            }
        }
    }
}
