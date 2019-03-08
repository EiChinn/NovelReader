package com.example.newbiechen.ireader.viewmodel

import androidx.lifecycle.ViewModel
import com.example.newbiechen.ireader.model.repository.BookCategoryDetailRepository

class BookCategoryDetailViewModel(bookCategoryDetailRepository: BookCategoryDetailRepository) : ViewModel() {
    val isRequestInProgress = bookCategoryDetailRepository.isRequestInProgress
    val toastMsg = bookCategoryDetailRepository.toastMsg
    var gender: String = "male"
    var major: String = ""
    val mins = bookCategoryDetailRepository.fetchBookCategoryMin(gender, major)
}