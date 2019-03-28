package com.example.newbiechen.ireader.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.ui.activity.BookListActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BookListRepository private constructor(private val remoteRepository: RemoteRepository) {
    val isRequestInProgress = MutableLiveData<Boolean>()
    val toastMsg = MutableLiveData<String>()
    fun fetchBookListTags(): LiveData<List<BookListActivity.MultiTagItem>> {
        isRequestInProgress.postValue(true)
        val result = MutableLiveData<List<BookListActivity.MultiTagItem>>()
        remoteRepository.bookTags.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .map {response ->
                    if (response.ok) {
                        val result = mutableListOf<BookListActivity.MultiTagItem>()
                        result.add(BookListActivity.MultiTagItem(BookListActivity.MultiTagItem.TYPE_CATEGORY, "行别"))
                        result.add(BookListActivity.MultiTagItem(BookListActivity.MultiTagItem.TYPE_TAG, "男生"))
                        result.add(BookListActivity.MultiTagItem(BookListActivity.MultiTagItem.TYPE_TAG, "女生"))
                        response.data.forEach {tag ->
                            result.add(BookListActivity.MultiTagItem(BookListActivity.MultiTagItem.TYPE_CATEGORY, tag.name))
                            tag.tags.forEach {
                                result.add(BookListActivity.MultiTagItem(BookListActivity.MultiTagItem.TYPE_TAG, it))

                            }
                        }
                        result
                    } else {
                        throw IllegalStateException("fetch book list tags failed")
                    }
                }
                .subscribe(
                        { response ->
                            isRequestInProgress.postValue(false)
                            result.postValue(response)

                        },
                        { error ->
                            isRequestInProgress.postValue(false)
                            toastMsg.postValue(error.toString())
                        }
                )
        return result
    }

    /*fun fetchBookList(): List<TagBookBean> {

    }*/
    companion object {
        // For singleton instantiation
        @Volatile private var instance: BookListRepository? = null
        fun getInstance(remoteRepository: RemoteRepository) = instance ?: synchronized(this) {
            instance ?: BookListRepository(remoteRepository).also { instance = it }
        }
    }
}