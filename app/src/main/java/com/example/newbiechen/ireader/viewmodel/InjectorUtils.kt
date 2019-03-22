package com.example.newbiechen.ireader.viewmodel

import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.model.repository.BookCategoryDetailRepository
import com.example.newbiechen.ireader.model.repository.BookCategoryRepository
import com.example.newbiechen.ireader.model.repository.LeaderBoardBookRepository
import com.example.newbiechen.ireader.model.repository.LeaderBoardRepository

object InjectorUtils {
    private fun getBookCategoryRepository() = BookCategoryRepository.getInstance(RemoteRepository.instance)
    private fun getBookCategoryDetailRepository() = BookCategoryDetailRepository.getInstance(RemoteRepository.instance)
    private fun getLeaderBoardRepository() = LeaderBoardRepository.getInstance(RemoteRepository.instance)
    private fun getLeaderBoardBookRepository() = LeaderBoardBookRepository.getInstance(RemoteRepository.instance)

    fun provideBookCategoryViewModelFactory() = BookCategoryViewModelFactory(getBookCategoryRepository())
    fun provideBookCategoryDetailViewModelFactory() = BookCategoryDetailViewModelFactory(getBookCategoryDetailRepository())
    fun provideLeaderBoardRepositoryFactory() = LeaderBoardViewModelFactory(getLeaderBoardRepository())
    fun provideLeaderBoardBookReposityFactory() = LeaderBoardBookViewModelFactory(getLeaderBoardBookRepository())
}