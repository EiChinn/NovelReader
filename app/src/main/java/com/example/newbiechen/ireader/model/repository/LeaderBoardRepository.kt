package com.example.newbiechen.ireader.model.repository

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.example.newbiechen.ireader.model.bean.LeaderBoardLabel
import com.example.newbiechen.ireader.model.bean.LeaderBoardName
import com.example.newbiechen.ireader.model.remote.RemoteRepository
import com.example.newbiechen.ireader.viewmodel.BaseViewModelData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LeaderBoardRepository private constructor(private val remoteRepository: RemoteRepository) {

    fun fetchLeaderBoard(): BaseViewModelData<List<MultiItemEntity>> {
        val isRequestInProgress = MutableLiveData<Boolean>()
        val toastMsg = MutableLiveData<String>()
        val data = MutableLiveData<List<MultiItemEntity>>()
        isRequestInProgress.postValue(true)
        remoteRepository.billboardPackage.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map { response ->
                    val list = mutableListOf<MultiItemEntity>()
                    response.male?.let { male ->
                        if (male.isNotEmpty()) {
                            list.add(LeaderBoardLabel("男生"))
                            val map = male.groupBy{
                                        if (it.isCollapse) {
                                            "other"
                                        } else {
                                            "name"
                                        }
                                    }
                            list.addAll(map["name"]!!)
                            val otherLeaderBoard = LeaderBoardName("", "别人家的排行榜", "")
                            otherLeaderBoard.subItems = map["other"]
                            list.add(otherLeaderBoard)
                        }
                    }
                    response.female?.let {femle ->
                        if (femle.isNotEmpty()) {
                            list.add(LeaderBoardLabel("女生"))
                            val map = femle.groupBy{
                                        if (it.isCollapse) {
                                            "other"
                                        } else {
                                            "name"
                                        }
                                    }
                            list.addAll(map["name"]!!)
                            val otherLeaderBoard = LeaderBoardName("", "别人家的排行榜", "")
                            otherLeaderBoard.subItems = map["other"]
                            list.add(otherLeaderBoard)
                        }
                    }
                    list
                }
                .subscribe(
                        {response ->
                            isRequestInProgress.postValue(false)
                            data.postValue(response)
                        },
                        {error ->
                            isRequestInProgress.postValue(false)
                            toastMsg.postValue(error.toString())
                        }
                )
        return BaseViewModelData(isRequestInProgress, toastMsg, data)
    }

    companion object {
        // For singleton instantiation
        @Volatile private var instance: LeaderBoardRepository? = null
        fun getInstance(remoteRepository: RemoteRepository) = instance ?: synchronized(this) {
            instance ?: LeaderBoardRepository(remoteRepository).also { instance = it }
        }
    }

}