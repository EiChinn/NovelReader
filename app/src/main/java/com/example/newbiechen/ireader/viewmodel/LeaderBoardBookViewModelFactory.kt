package com.example.newbiechen.ireader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newbiechen.ireader.model.repository.LeaderBoardBookRepository

class LeaderBoardBookViewModelFactory(private val leaderBoardBookRepository: LeaderBoardBookRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LeaderBoardBookViewModel(leaderBoardBookRepository) as T
    }

}