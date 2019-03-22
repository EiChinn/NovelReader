package com.example.newbiechen.ireader.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

data class BaseViewModelData<T>(
        val isRequestInProgress: MutableLiveData<Boolean>,
        val toastMsg: MutableLiveData<String>,
        val data: LiveData<T>
)