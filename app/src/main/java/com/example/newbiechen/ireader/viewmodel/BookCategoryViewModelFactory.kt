package com.example.newbiechen.ireader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newbiechen.ireader.model.repository.BookCategoryRepository

class BookCategoryViewModelFactory(private val bookCategoryRepository: BookCategoryRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BookCategoryViewModel(bookCategoryRepository) as T
    }
}