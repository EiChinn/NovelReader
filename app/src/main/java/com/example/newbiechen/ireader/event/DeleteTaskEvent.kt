package com.example.newbiechen.ireader.event

import com.example.newbiechen.ireader.db.entity.CollBook

data class DeleteTaskEvent(val collBook: CollBook)