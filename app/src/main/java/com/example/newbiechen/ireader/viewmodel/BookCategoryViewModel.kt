package com.example.newbiechen.ireader.viewmodel

import androidx.lifecycle.ViewModel
import com.example.newbiechen.ireader.model.repository.BookCategoryRepository

class BookCategoryViewModel(bookCategoryRepository: BookCategoryRepository) : ViewModel() {
    val bookCategory = bookCategoryRepository.fetchBookCategory()
    val isRequestInProgress = bookCategoryRepository.isRequestInProgress
    val toastMsg = bookCategoryRepository.toastMsg
}