package com.example.newbiechen.ireader.event

import com.example.newbiechen.ireader.model.flag.BookDistillate
import com.example.newbiechen.ireader.model.flag.BookSort
import com.example.newbiechen.ireader.model.flag.BookType

data class SelectorEvent(val distillate: BookDistillate, val type: BookType, val sort: BookSort)