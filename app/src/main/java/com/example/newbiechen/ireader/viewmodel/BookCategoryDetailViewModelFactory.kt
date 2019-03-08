package com.example.newbiechen.ireader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newbiechen.ireader.model.repository.BookCategoryDetailRepository

class BookCategoryDetailViewModelFactory(private val bookCategoryDetailRepository: BookCategoryDetailRepository)
    : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BookCategoryDetailViewModel(bookCategoryDetailRepository) as T
    }

}