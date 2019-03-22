package com.example.newbiechen.ireader.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.newbiechen.ireader.model.repository.LeaderBoardBookRepository

class LeaderBoardBookViewModel(leaderBoardBookRepository: LeaderBoardBookRepository) : ViewModel() {
    val id = MutableLiveData<String>()
    private val dataWrapper = Transformations.map(id) {
        leaderBoardBookRepository.fetchLeaderBoardBook(it)
    }
    val isRequestInProgress = Transformations.switchMap(dataWrapper){ it.isRequestInProgress }
    val toastMsg = Transformations.switchMap(dataWrapper){ it.toastMsg }
    val data = Transformations.switchMap(dataWrapper){ it.data }
}