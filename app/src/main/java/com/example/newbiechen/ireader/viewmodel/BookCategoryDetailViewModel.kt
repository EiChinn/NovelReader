package com.example.newbiechen.ireader.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.example.newbiechen.ireader.model.bean.SortBookBean
import com.example.newbiechen.ireader.model.repository.BookCategoryDetailRepository

class BookCategoryDetailViewModel(private val bookCategoryDetailRepository: BookCategoryDetailRepository) : ViewModel() {
    val currentType = MutableLiveData<String>()
    val currentMinor = MutableLiveData<String>()
    private val bookList = MediatorLiveData<BookCategoryList>()
    init {
        bookList.addSource(currentType) {
            if (it.isNotBlank() && currentMinor.value != null) {
                bookList.postValue(bookCategoryDetailRepository.fetchBooks(gender, currentType.value ?: "", major, currentMinor.value ?: "", 0, 10))
            }
        }

        bookList.addSource(currentMinor) {
            if (it.isNotBlank() && currentType.value != null) {
                bookList.postValue(bookCategoryDetailRepository.fetchBooks(gender, currentType.value ?: "", major, currentMinor.value ?: "", 0, 10))
            }
        }
    }
    val isRequestInProgress = Transformations.switchMap(bookList) { it.isRequestInProgress }
    val toastMsg = Transformations.switchMap(bookList) { it.toastMsg }
    var gender = ""
    var major = ""
    var mins = MutableLiveData<List<String>>()
    var books = Transformations.switchMap(bookList) { it.books }

    fun fetchMins() {
        if (gender.isNotBlank() and major.isNotBlank()) {
            mins = bookCategoryDetailRepository.fetchBookCategoryMin(gender, major)
        }
    }




}

data class BookCategoryList(
        val isRequestInProgress: LiveData<Boolean>,
        val toastMsg: LiveData<String>,
        val books: LiveData<PagedList<SortBookBean>>
)