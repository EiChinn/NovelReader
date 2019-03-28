package com.example.newbiechen.ireader.viewmodel

import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.model.repository.*

object InjectorUtils {
    private fun getBookCategoryRepository() = BookCategoryRepository.getInstance(RemoteRepository.instance)
    private fun getBookCategoryDetailRepository() = BookCategoryDetailRepository.getInstance(RemoteRepository.instance)
    private fun getLeaderBoardRepository() = LeaderBoardRepository.getInstance(RemoteRepository.instance)
    private fun getLeaderBoardBookRepository() = LeaderBoardBookRepository.getInstance(RemoteRepository.instance)
    private fun getBookListRepository() = BookListRepository.getInstance(RemoteRepository.instance)

    fun provideBookCategoryViewModelFactory() = BookCategoryViewModelFactory(getBookCategoryRepository())
    fun provideBookCategoryDetailViewModelFactory() = BookCategoryDetailViewModelFactory(getBookCategoryDetailRepository())
    fun provideLeaderBoardViewModelFactory() = LeaderBoardViewModelFactory(getLeaderBoardRepository())
    fun provideLeaderBoardBookViewModelFactory() = LeaderBoardBookViewModelFactory(getLeaderBoardBookRepository())
    fun provideBookListViewModelFactory() = BookListViewModelFactory(getBookListRepository())
}