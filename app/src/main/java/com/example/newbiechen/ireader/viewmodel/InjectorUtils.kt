package com.example.newbiechen.ireader.viewmodel

import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.model.repository.BookCategoryDetailRepository
import com.example.newbiechen.ireader.model.repository.BookCategoryRepository

object InjectorUtils {
    private fun getBookCategoryRepository() = BookCategoryRepository.getInstance(RemoteRepository.instance)
    private fun getBookCategoryDetailRepository() = BookCategoryDetailRepository.getInstance(RemoteRepository.instance)

    fun provideBookCategoryViewModelFactory() = BookCategoryViewModelFactory(getBookCategoryRepository())
    fun provideBookCategoryDetailViewModelFactory() = BookCategoryDetailViewModelFactory(getBookCategoryDetailRepository())
}