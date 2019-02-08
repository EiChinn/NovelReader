package com.example.newbiechen.ireader.event

import com.example.newbiechen.ireader.model.bean.CollBookBean

data class DeleteResponseEvent(val isDelete: Boolean, val collBook: CollBookBean)