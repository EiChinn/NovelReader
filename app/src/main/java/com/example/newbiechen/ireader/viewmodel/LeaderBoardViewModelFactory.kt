package com.example.newbiechen.ireader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newbiechen.ireader.model.repository.LeaderBoardRepository

class LeaderBoardViewModelFactory(private val leaderBoardRepository: LeaderBoardRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LeaderBoardViewModel(leaderBoardRepository) as T
    }
}