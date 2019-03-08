package com.example.newbiechen.ireader.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newbiechen.ireader.model.bean.BookSortBean
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.ui.adapter.BookCategoryMultiItemEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BookCategoryRepository private constructor(private val remoteRepository: RemoteRepository) {
    val isRequestInProgress = MutableLiveData<Boolean>()
    val toastMsg = MutableLiveData<String>()
    fun fetchBookCategory(): LiveData<List<BookCategoryMultiItemEntity>> {
        isRequestInProgress.postValue(true)
        val result = MutableLiveData<List<BookCategoryMultiItemEntity>>()
        val disposable = remoteRepository.bookSortPackage
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    val response = mutableListOf<BookCategoryMultiItemEntity>()
                    response.add(BookCategoryMultiItemEntity(BookCategoryMultiItemEntity.ITEM_LABEL, BookSortBean("男生", 0)))
                    it.male.forEach { maleCategory ->
                        response.add(BookCategoryMultiItemEntity(BookCategoryMultiItemEntity.ITEM_CATEGORY, maleCategory, "male"))
                    }
                    response.add(BookCategoryMultiItemEntity(BookCategoryMultiItemEntity.ITEM_LABEL, BookSortBean("女生", 0)))
                    it.female.forEach { femaleCategory ->
                        response.add(BookCategoryMultiItemEntity(BookCategoryMultiItemEntity.ITEM_CATEGORY, femaleCategory, "female"))
                    }
                    response
                }
                .subscribe (
                        { response ->
                            result.postValue(response)
                            isRequestInProgress.postValue(false)
                        },
                        {error ->
                            isRequestInProgress.postValue(false)
                            toastMsg.postValue(error.toString())
                        })

        return result
    }

    companion object {
        // For singleton instantiation
        @Volatile private var instance: BookCategoryRepository? = null
        fun getInstance(remoteRepository: RemoteRepository) = instance ?: synchronized(this) {
            instance ?: BookCategoryRepository(remoteRepository).also { instance = it }
        }
    }
}