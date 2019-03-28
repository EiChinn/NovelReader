package com.example.newbiechen.ireader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newbiechen.ireader.model.repository.BookListRepository

class BookListViewModelFactory(private val bookListRepository: BookListRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BookListViewModel(bookListRepository) as T
    }
}