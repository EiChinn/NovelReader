package com.example.newbiechen.ireader.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newbiechen.ireader.model.bean.SortBookBean
import com.example.newbiechen.ireader.model.repository.BookCategoryDetailRepository

class BookCategoryDetailViewModel(private val bookCategoryDetailRepository: BookCategoryDetailRepository) : ViewModel() {
    val isRequestInProgress = bookCategoryDetailRepository.isRequestInProgress
    val toastMsg = bookCategoryDetailRepository.toastMsg
    var gender = "male"
    var major = ""
    var mins = MutableLiveData<List<String>>()
    var currentType = MutableLiveData<String>()
    var currentMinor = MutableLiveData<String>()
    var books = MutableLiveData<List<SortBookBean>>()

    fun fetchMins() {
        if (gender.isNotBlank() and major.isNotBlank()) {
            mins = bookCategoryDetailRepository.fetchBookCategoryMin(gender, major)
        }
    }

    fun fetBooks() {
        if (currentType.value != null && currentMinor.value != null) {
            bookCategoryDetailRepository.fetchBooks(gender, currentType.value!!, major, currentMinor.value!!, 0, 20, books)
        }
    }

}