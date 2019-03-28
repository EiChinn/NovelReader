package com.example.newbiechen.ireader.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newbiechen.ireader.model.bean.TagBookBean
import com.example.newbiechen.ireader.model.repository.BookListRepository
import com.example.newbiechen.ireader.ui.activity.BookListActivity

class BookListViewModel(private val bookListRepository: BookListRepository) : ViewModel() {
    val isRequestInProgress = bookListRepository.isRequestInProgress
    val toastMsg = bookListRepository.toastMsg

    var tags: LiveData<List<BookListActivity.MultiTagItem>>? = null
    val big5Tags = MutableLiveData<MutableList<String>>()
    val currentCategory = MutableLiveData<String>()
    val currentTag = MutableLiveData<String>() // todo 注意请求次数

    val bookList = MediatorLiveData<List<TagBookBean>>()

    init {
        bookList.addSource(currentCategory) {
            if (currentTag.value != null) {
//                bookList.postValue()
                Log.i("tag", "current category changed, fetch book list")
            }
        }
        bookList.addSource(currentTag) {
            if (currentCategory.value != null) {
//                bookList.postValue()
                Log.i("tag", "current tag changed, fetch book list")
            }
        }
    }

    fun fetchTags() {
        tags = bookListRepository.fetchBookListTags()
    }

    fun refreshBig5Tags(newTag: String) {
        val oldTags = big5Tags.value
        val index = oldTags?.indexOf(newTag) ?: -1
        if (index < 0) {
            oldTags?.removeAt(4)
            oldTags?.add(0, newTag)
            big5Tags.postValue(oldTags)
        }
        currentTag.postValue(newTag)

    }

}
