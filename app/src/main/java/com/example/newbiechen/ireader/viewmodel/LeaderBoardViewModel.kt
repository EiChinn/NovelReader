package com.example.newbiechen.ireader.viewmodel

import androidx.lifecycle.ViewModel
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.newbiechen.ireader.model.bean.packages.BillboardPackage
import com.example.newbiechen.ireader.model.repository.LeaderBoardRepository

class LeaderBoardViewModel(leaderBoardRepository: LeaderBoardRepository) : ViewModel() {
    val dataWrapper: BaseViewModelData<List<MultiItemEntity>> = leaderBoardRepository.fetchLeaderBoard()
}