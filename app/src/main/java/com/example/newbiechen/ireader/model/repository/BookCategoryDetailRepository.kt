package com.example.newbiechen.ireader.model.repository

import androidx.lifecycle.MutableLiveData
import com.example.newbiechen.ireader.model.bean.SortBookBean
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BookCategoryDetailRepository private constructor(private val remoteRepository: RemoteRepository) {
    val isRequestInProgress = MutableLiveData<Boolean>()
    val toastMsg = MutableLiveData<String>()
    fun fetchBookCategoryMin(gender: String, majorCategory: String): MutableLiveData<List<String>> {
        val result = MutableLiveData<List<String>>()
        isRequestInProgress.postValue(true)
        val disposable = remoteRepository.bookSubSortPackage.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map{ if (gender == "male") it.male else it.female}
                .map{ it.first { gender -> gender.major == majorCategory }.mins  }
                .subscribe (
                        { response ->
                            response.add(0, "全部")
                            result.postValue(response)
                            isRequestInProgress.postValue(false)
                        },
                        {error ->
                            isRequestInProgress.postValue(false)
                            toastMsg.postValue(error.toString())
                        })
        return result

    }

    fun fetchBooks(gender: String, type: String, major: String, minor: String, start: Int, limit: Int, books: MutableLiveData<List<SortBookBean>>){
        isRequestInProgress.postValue(true)
        val disposable = remoteRepository.getSortBooks(gender, type, major, minor, start, limit)
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                        {response ->
                            books.postValue(response)
                            isRequestInProgress.postValue(false)

                        },
                        {error ->
                            isRequestInProgress.postValue(false)
                            toastMsg.postValue(error.toString())
                        }
                )
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
